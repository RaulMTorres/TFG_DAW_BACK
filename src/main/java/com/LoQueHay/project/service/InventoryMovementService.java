package com.LoQueHay.project.service;

import com.LoQueHay.project.Specification.InventoryMovementSpecifications;
import com.LoQueHay.project.dto.dashboard_dtos.MonthlyMovementSummaryDTO;
import com.LoQueHay.project.dto.dashboard_dtos.MonthlySalesPurchasesDTO;
import com.LoQueHay.project.dto.inventory_movements_dtos.InventoryMovementRequestDTO;
import com.LoQueHay.project.dto.inventory_movements_dtos.InventoryMovementResponseDTO;
import com.LoQueHay.project.dto.product_stock_dtos.ProductStockRequestDTO;
import com.LoQueHay.project.exception.BadRequestException;
import com.LoQueHay.project.exception.InsufficientStockException;
import com.LoQueHay.project.exception.InvalidMovementDetailsException;
import com.LoQueHay.project.exception.ResourceNotFoundException;
import com.LoQueHay.project.mappers.InventoryMovementMapper;
import com.LoQueHay.project.model.*;
import com.LoQueHay.project.repository.*;
import com.LoQueHay.project.util.AuthUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryMovementService {

    private final InventoryMovementRepository movementRepository;
    private final InventoryMovementDetailRepository detailRepository;
    private final ProductService productService;
    private final WarehouseService warehouseService;
    private final AuthUtils authUtils;
    private final ProductStockService stockService;

    public InventoryMovementService(InventoryMovementRepository movementRepository,
                                    InventoryMovementDetailRepository detailRepository,
                                    ProductService productService,
                                    WarehouseService warehouseService,
                                    AuthUtils authUtils,
                                    ProductStockService stockService) {
        this.movementRepository = movementRepository;
        this.detailRepository = detailRepository;
        this.productService = productService;
        this.warehouseService = warehouseService;
        this.authUtils = authUtils;
        this.stockService = stockService;
    }

    @Transactional
    public InventoryMovement createMovement(InventoryMovementRequestDTO dto) {
        MyUserEntity currentUser = authUtils.getCurrentUser();


        InventoryMovement movement = new InventoryMovement();
        //Guardo el tipo de movimiento (Entrada o Salida)
        movement.setMovementType(dto.getMovementType());

        //Guardo el documento de referencia
        movement.setReferenceDocument(dto.getReferenceDocument());

        //Guardo la nota
        movement.setNote(dto.getNote());

        //Guardo el almacen al que pertence
        movement.setWarehouse(warehouseService.getById(dto.getWarehouseId()));

        movement.setOwner(currentUser.getOwner() != null ? currentUser.getOwner() : currentUser);

        movement.setCreatedBy(currentUser);

        movementRepository.save(movement);


        List<InventoryMovementDetail> details = new ArrayList<>();

        //Aqui vamos a empezar a validar los detalles del movimiento (Productos y Cantidades)
        // En dependencia si es entrada o salida validamos distinto

        //Validaciones en comun
        //La cantidad de ninguno de los elementos puede ser 0

        //Si es entrada
        if(dto.getMovementType() == MovementType.IN) {

            //Validamos que traiga los detalles necesarios para crear el movimiento
            if (dto.getEntryDetails() == null || dto.getEntryDetails().isEmpty()) {
                throw new InvalidMovementDetailsException("Debe enviar al menos un detalle para entradas");
            }
            if (dto.getExitDetails() != null && !dto.getExitDetails().isEmpty()) {
                throw new InvalidMovementDetailsException("Para crear un movement in debe incluir solo las entryDetails (Las exitDetail son solo para movement out)");
            }



            for (var detailDTO : dto.getEntryDetails()) {
                Product product = productService.getById(detailDTO.getProductId());

                String lotNumber = detailDTO.getLotNumber();
                if (lotNumber == null || lotNumber.isEmpty()) {
                    lotNumber = "AUTO-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                }

                LocalDate expirationDate = null;
                if (detailDTO.getExpirationDate() != null && !detailDTO.getExpirationDate().isEmpty()) {
                    if(!product.getHasExpirationDate()){
                        throw new BadRequestException("El producto: "+product.getName()+" no tiene expiracion");
                    }
                    expirationDate = LocalDate.parse(detailDTO.getExpirationDate());
                }



                var existingStockOpt = stockService.findByProductWarehouseAndLot(
                        product.getId(),
                        movement.getWarehouse().getId(),
                        lotNumber
                );

                if (existingStockOpt.isPresent()) {
                    ProductStock existingStock = existingStockOpt.get();

                    if (movement.getMovementType() == MovementType.IN) {
                        existingStock.setQuantity(existingStock.getQuantity() + detailDTO.getQuantity());
                    }

                    stockService.save(existingStock);

                }
                else{
                    // si tod esta bien creamos nuestro stock asociado a este movimiento
                    ProductStockRequestDTO productStockRequestDTO = new ProductStockRequestDTO();
                    productStockRequestDTO.setQuantity(detailDTO.getQuantity());
                    productStockRequestDTO.setLotNumber(lotNumber);
                    productStockRequestDTO.setUnitCost(detailDTO.getUnitCost());
                    productStockRequestDTO.setWarehouseId(movement.getWarehouse().getId());
                    productStockRequestDTO.setExpirationDate(expirationDate);

                    stockService.create(product.getId(), productStockRequestDTO);
                }

                InventoryMovementDetail detail = new InventoryMovementDetail();
                detail.setProduct(product);
                detail.setQuantity(detailDTO.getQuantity());
                detail.setUnitCost(detailDTO.getUnitCost());
                detail.setLotNumber(lotNumber);
                detail.setExpirationDate(expirationDate);
                detail.setMovement(movement);
                movement.getDetails().add(detail);

            }
        }
        //Si es salida
        else if(dto.getMovementType() == MovementType.OUT) {
            //Validamos que traiga los detalles necesarios para crear el movimiento
            if (dto.getExitDetails() == null || dto.getExitDetails().isEmpty()) {
                throw new InvalidMovementDetailsException("Debe enviar al menos un detalle para la salida");
            }
            if (dto.getEntryDetails() != null && !dto.getEntryDetails().isEmpty()) {
                throw new InvalidMovementDetailsException("Para crear un movement in debe incluir solo las entryDetails (Las exitDetail son solo para movement out)");
            }



            for(var detailDTO : dto.getExitDetails()) {

                Product product = productService.getById(detailDTO.getProductId());

                //Si es una salida comprobamos que haya stock disponible para retirar de ese producto en ese almacen
                // Si no hay lanzamos excepcion
                if(movement.getMovementType() == MovementType.OUT && !stockService.IsEnoughStock(detailDTO.getQuantity(),product,movement.getWarehouse())){
                    throw new InsufficientStockException(product.getId(), product.getName());
                }
                // Si hay stock suficiente descontamos

                int quantityToRemove = detailDTO.getQuantity();

                // Obtenemos los lotes disponibles ordenados por fecha de entrada (FIFO)
                List<ProductStock> availableStocks = stockService.getAvailableStocks(product, movement.getWarehouse());

                for(ProductStock stock : availableStocks) {
                    if(quantityToRemove <= 0) break;

                    int stockQty = stock.getQuantity();

                    if(stockQty >= quantityToRemove) {
                        // Reducimos stock del lote actual
                        stock.setQuantity(stockQty - quantityToRemove);
                        stockService.save(stock); // actualizar en DB
                        quantityToRemove = 0;
                    } else {
                        // Consumimos todo el lote y seguimos con el siguiente
                        stock.setQuantity(0);
                        stockService.save(stock);
                        quantityToRemove -= stockQty;
                    }
                }

                InventoryMovementDetail detail = new InventoryMovementDetail();
                detail.setProduct(product);
                detail.setQuantity(detailDTO.getQuantity());
                detail.setMovement(movement);
                detail.setSellPriceUnit(detailDTO.getSellPriceUnit());
                movement.getDetails().add(detail);

            }
        }


        return movement;
    }

    public Page<InventoryMovement> getPagedMovementsByProduct(
            Long productId,
            String reference,
            String movementType,
            Long warehouseId,
            Integer quantity,
            Double unitCost,
            int page,
            int size
    ) {
        MyUserEntity _u = authUtils.getCurrentUser(); Long ownerId = _u.getOwner() != null ? _u.getOwner().getId() : _u.getId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<InventoryMovement> spec = InventoryMovementSpecifications.belongsToOwner(ownerId);

        // 🔍 Filtros estándar
        if (reference != null && !reference.isEmpty()) {
            spec = spec.and(InventoryMovementSpecifications.referenceContains(reference));
        }

        if (movementType != null && !movementType.isEmpty()) {
            spec = spec.and(InventoryMovementSpecifications.movementTypeEquals(movementType));
        }

        if (warehouseId != null) {
            spec = spec.and(InventoryMovementSpecifications.warehouseId(warehouseId));
        }

        // 🔍 Filtro por producto
        spec = spec.and(InventoryMovementSpecifications.hasProduct(productId));

        // 🔍 Filtro por detalles: cantidad y unitCost
        if (quantity != null) {
            spec = spec.and(InventoryMovementSpecifications.quantityEquals(quantity));
        }

        if (unitCost != null) {
            spec = spec.and(InventoryMovementSpecifications.unitCostEquals(unitCost));
        }

        return movementRepository.findAll(spec, pageable);
    }




    public InventoryMovement getById(Long id){
        MyUserEntity user = authUtils.getCurrentUser();

        return movementRepository.findByIdAndOwnerId(id, user.getOwner().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Movement not found for this owner"));
    }

    public Page<InventoryMovement> getPagedMovements(
            String reference,
            String movementType,
            Long warehouseId,
            int page,
            int size
    ) {
        MyUserEntity _u = authUtils.getCurrentUser(); Long ownerId = _u.getOwner() != null ? _u.getOwner().getId() : _u.getId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<InventoryMovement> spec = InventoryMovementSpecifications.belongsToOwner(ownerId);

        // 🔍 Filtro por referencia (referenceDocument)
        if (reference != null && !reference.isEmpty()) {
            spec = spec.and(InventoryMovementSpecifications.referenceContains(reference));
        }

        // 🔍 Filtro por tipo de movimiento (IN / OUT)
        if (movementType != null && !movementType.isEmpty()) {
            spec = spec.and(InventoryMovementSpecifications.movementTypeEquals(movementType));
        }

        // 🔍 Filtro por almacén
        if (warehouseId != null) {
            spec = spec.and(InventoryMovementSpecifications.warehouseId(warehouseId));
        }

        return movementRepository.findAll(spec, pageable);
    }




    /**
     * Devuelve la cantidad de ventas (OUT) del último mes para un owner.
     */
    public Long getSalesThisMonth(Long ownerId) {
        LocalDateTime startOfMonth = LocalDateTime.now()
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0);
        return movementRepository.countSalesLastMonth(ownerId, startOfMonth);
    }

    /**
     * Devuelve la cantidad de compras/entradas (IN) del último mes para un owner.
     */
    public Long getPurchasesThisMonth(Long ownerId) {
        LocalDateTime startOfMonth = LocalDateTime.now()
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0);
        return movementRepository.countPurchasesLastMonth(ownerId, startOfMonth);
    }


    public MonthlySalesPurchasesDTO getMonthlySalesAndPurchases() {
        MyUserEntity _u = authUtils.getCurrentUser(); Long ownerId = _u.getOwner() != null ? _u.getOwner().getId() : _u.getId();

        LocalDateTime startDate = LocalDateTime.now().minusMonths(6);

        List<InventoryMovement> movements = movementRepository.findAllMovementsWithDetails(ownerId, startDate);

        // Mapa: YearMonth -> { tipoMovimiento -> total }
        Map<YearMonth, Map<MovementType, Double>> monthlyTotals = new HashMap<>();

        for (InventoryMovement im : movements) {
            YearMonth yearMonth = YearMonth.from(im.getCreatedAt());
            MovementType movementType = im.getMovementType();

            double movementTotal = im.getDetails().stream()
                    .mapToDouble(d -> {
                        if (MovementType.OUT.equals(movementType)) {
                            return d.getQuantity() * (d.getSellPriceUnit() != null ? d.getSellPriceUnit() : 0);
                        } else if (MovementType.IN.equals(movementType)) {
                            return d.getQuantity() * (d.getUnitCost() != null ? d.getUnitCost() : 0);
                        }
                        return 0;
                    })
                    .sum();

            monthlyTotals
                    .computeIfAbsent(yearMonth, k -> new HashMap<>())
                    .merge(movementType, movementTotal, Double::sum);
        }

        List<YearMonth> sortedMonths = monthlyTotals.keySet().stream()
                .sorted()
                .collect(Collectors.toList());

        List<String> months = new ArrayList<>();
        List<Double> sales = new ArrayList<>();
        List<Double> purchases = new ArrayList<>();

        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.forLanguageTag("es"));

        for (YearMonth ym : sortedMonths) {
            months.add(ym.format(monthFormatter));
            sales.add(monthlyTotals.get(ym).getOrDefault(MovementType.OUT, 0.0));
            purchases.add(monthlyTotals.get(ym).getOrDefault(MovementType.IN, 0.0));
        }

        return new MonthlySalesPurchasesDTO(months, sales, purchases);
    }



    @Transactional
    public void deleteMultiple(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        MyUserEntity currentUser = authUtils.getCurrentUser();
        Long ownerId = currentUser.getOwner() != null ? currentUser.getOwner().getId() : currentUser.getId();

        List<Long> deletableIds = new ArrayList<>();

        for (Long id : ids) {
            if (id == null) continue;

            // Validar que exista y pertenezca al owner
            InventoryMovement movement = movementRepository.findByIdAndOwnerId(id, ownerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Movement not found for this owner: " + id));

            deletableIds.add(movement.getId());
        }

        if (!deletableIds.isEmpty()) {
            movementRepository.deleteAllById(deletableIds);
        }
    }
}
