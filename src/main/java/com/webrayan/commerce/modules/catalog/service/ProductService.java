package com.webrayan.commerce.modules.catalog.service;

import com.webrayan.commerce.modules.catalog.entity.Product;
import com.webrayan.commerce.modules.catalog.entity.Category;
import com.webrayan.commerce.modules.catalog.entity.ProductAttributeValue;
import com.webrayan.commerce.modules.catalog.entity.CategoryAttribute;
import com.webrayan.commerce.modules.catalog.enums.AttributeType;
import com.webrayan.commerce.modules.catalog.enums.ProductStatus;
import com.webrayan.commerce.modules.catalog.repository.ProductRepository;
import com.webrayan.commerce.modules.catalog.repository.ProductAttributeValueRepository;
import com.webrayan.commerce.modules.catalog.repository.CategoryAttributeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductAttributeValueRepository productAttributeValueRepository;
    private final CategoryAttributeRepository categoryAttributeRepository;

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> getActiveProducts(Pageable pageable) {
        return productRepository.findByIsActiveTrueAndStatus(ProductStatus.PUBLISHED, pageable);
    }

    public Page<Product> getProductsByCategory(Category category, Pageable pageable) {
        return productRepository.findByCategoryAndIsActiveTrueAndStatus(category, ProductStatus.PUBLISHED, pageable);
    }

    public List<Product> getFeaturedProducts() {
        return productRepository.findFeaturedProducts(ProductStatus.PUBLISHED);
    }

//    public Page<Product> searchProducts(String keyword, Pageable pageable) {
//        return productRepository.searchProducts(keyword, ProductStatus.PUBLISHED, pageable);
//    }

    public Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(ProductStatus.PUBLISHED, minPrice, maxPrice, pageable);
    }

    public Page<Product> getProductsByUser(Long userId, Pageable pageable) {
        return productRepository.findBySeller(userId, pageable);
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts(ProductStatus.PUBLISHED);
    }

    public List<Product> getOutOfStockProducts() {
        return productRepository.findOutOfStockProducts(ProductStatus.PUBLISHED);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> getProductBySlug(String slug) {
        return productRepository.findBySlug(slug);
    }

    public Product createProduct(Product product) {
        validateProduct(product);
        generateSlugIfEmpty(product);
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        product.setName(productDetails.getName());
        product.setShortDescription(productDetails.getShortDescription());
        product.setDescription(productDetails.getDescription());
        product.setSlug(productDetails.getSlug());
        product.setSku(productDetails.getSku());
        product.setPrice(productDetails.getPrice());
        product.setDiscountPrice(productDetails.getDiscountPrice());
        product.setStockQuantity(productDetails.getStockQuantity());
        product.setMinStockLevel(productDetails.getMinStockLevel());
        product.setIsActive(productDetails.getIsActive());
        product.setIsFeatured(productDetails.getIsFeatured());
        product.setIsDigital(productDetails.getIsDigital());
        product.setManageStock(productDetails.getManageStock());
        product.setStatus(productDetails.getStatus());
        product.setMetaTitle(productDetails.getMetaTitle());
        product.setMetaDescription(productDetails.getMetaDescription());
        product.setWeight(productDetails.getWeight());
        product.setLength(productDetails.getLength());
        product.setWidth(productDetails.getWidth());
        product.setHeight(productDetails.getHeight());
        product.setCategory(productDetails.getCategory());
        
        validateProduct(product);
        generateSlugIfEmpty(product);
        
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        // Delete all attribute values for this product
        productAttributeValueRepository.deleteByProduct(product);
        
        productRepository.delete(product);
    }

    public void toggleProductStatus(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        product.setIsActive(!product.getIsActive());
        productRepository.save(product);
    }

    public void updateProductStatus(Long id, ProductStatus status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        product.setStatus(status);
        productRepository.save(product);
    }

    public Product incrementViewCount(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        product.setViewsCount(product.getViewsCount() + 1);
        return productRepository.save(product);
    }

    public Product updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        if (product.getManageStock()) {
            product.setStockQuantity(quantity);
            
            // Auto-update status if out of stock
            if (quantity == 0) {
                product.setStatus(ProductStatus.OUT_OF_STOCK);
            } else if (product.getStatus() == ProductStatus.OUT_OF_STOCK) {
                product.setStatus(ProductStatus.PUBLISHED);
            }
        }
        
        return productRepository.save(product);
    }

    public void saveProductAttributes(Long productId, Map<Long, String> attributeValues) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        
        for (Map.Entry<Long, String> entry : attributeValues.entrySet()) {
            Long attributeId = entry.getKey();
            String value = entry.getValue();
            
            CategoryAttribute attribute = categoryAttributeRepository.findById(attributeId)
                    .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + attributeId));
            
            // Check if value already exists for this product and attribute
            Optional<ProductAttributeValue> existingValue = 
                    productAttributeValueRepository.findByProductAndAttribute(product, attribute);
            
            ProductAttributeValue attributeValue;
            if (existingValue.isPresent()) {
                attributeValue = existingValue.get();
            } else {
                attributeValue = new ProductAttributeValue();
                attributeValue.setProduct(product);
                attributeValue.setAttribute(attribute);
            }
            
            attributeValue.setValue(value);
            
            // Parse and set typed values based on attribute type
            parseAndSetTypedValue(attributeValue, value, attribute.getType());
            
            productAttributeValueRepository.save(attributeValue);
        }
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        
        if (product.getCategory() == null) {
            throw new IllegalArgumentException("Product must belong to a category");
        }
        
        if (product.getSeller() == null) {
            throw new IllegalArgumentException("Product must have a seller");
        }
        
        if (product.getSku() != null && 
            productRepository.existsBySku(product.getSku()) && 
            (product.getId() == null || !productRepository.findById(product.getId()).get().getSku().equals(product.getSku()))) {
            throw new IllegalArgumentException("Product with this SKU already exists");
        }
        
        if (product.getSlug() != null && 
            productRepository.existsBySlug(product.getSlug()) && 
            (product.getId() == null || !productRepository.findById(product.getId()).get().getSlug().equals(product.getSlug()))) {
            throw new IllegalArgumentException("Product with this slug already exists");
        }
        
        if (product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
        
        if (product.getDiscountPrice() != null && product.getDiscountPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Discount price cannot be negative");
        }
        
        if (product.getDiscountPrice() != null && product.getPrice() != null && 
            product.getDiscountPrice().compareTo(product.getPrice()) >= 0) {
            throw new IllegalArgumentException("Discount price must be less than regular price");
        }
        
        // Set default values
        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }
        if (product.getIsFeatured() == null) {
            product.setIsFeatured(false);
        }
        if (product.getIsDigital() == null) {
            product.setIsDigital(false);
        }
        if (product.getManageStock() == null) {
            product.setManageStock(true);
        }
        if (product.getStatus() == null) {
            product.setStatus(ProductStatus.DRAFT);
        }
        if (product.getStockQuantity() == null) {
            product.setStockQuantity(0);
        }
        if (product.getMinStockLevel() == null) {
            product.setMinStockLevel(0);
        }
        if (product.getViewsCount() == null) {
            product.setViewsCount(0L);
        }
        if (product.getSalesCount() == null) {
            product.setSalesCount(0L);
        }
    }

    private void generateSlugIfEmpty(Product product) {
        if (product.getSlug() == null || product.getSlug().trim().isEmpty()) {
            String slug = product.getName().toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("-+", "-")
                    .replaceAll("^-|-$", "");
            
            // Ensure slug is unique
            String originalSlug = slug;
            int counter = 1;
            while (productRepository.existsBySlug(slug)) {
                slug = originalSlug + "-" + counter++;
            }
            
            product.setSlug(slug);
        }
    }

    private void parseAndSetTypedValue(ProductAttributeValue attributeValue, String value, 
                                       AttributeType type) {
        try {
            switch (type) {
                case NUMBER:
                case DECIMAL:
                    if (value != null && !value.trim().isEmpty()) {
                        attributeValue.setNumericValue(new BigDecimal(value));
                    }
                    break;
                case BOOLEAN:
                case CHECKBOX:
                    if (value != null && !value.trim().isEmpty()) {
                        attributeValue.setBooleanValue(Boolean.parseBoolean(value));
                    }
                    break;
                case DATE:
                    if (value != null && !value.trim().isEmpty()) {
                        attributeValue.setDateValue(java.time.LocalDate.parse(value));
                    }
                    break;
                case DATETIME:
                    if (value != null && !value.trim().isEmpty()) {
                        attributeValue.setDatetimeValue(java.time.LocalDateTime.parse(value));
                    }
                    break;
                default:
                    // For text-based types, value is already set in the main value field
                    break;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid value for attribute type " + type + ": " + value);
        }
    }
    
    // متدهای جدید برای Admin Panel
    
    /**
     * جستجو در نام و SKU محصول
     */
    public Page<Product> searchProductsByName(String search, Pageable pageable) {
        return productRepository.findByNameContainingOrSkuContaining(search, pageable);
    }
    
    /**
     * جستجو در نام و SKU محصول بر اساس وضعیت
     */
    public Page<Product> searchProductsByNameAndStatus(String search, ProductStatus status, Pageable pageable) {
        return productRepository.findByNameContainingOrSkuContainingAndStatus(search, status, pageable);
    }
    
    /**
     * دریافت محصولات بر اساس وضعیت
     */
    public Page<Product> getProductsByStatus(ProductStatus status, Pageable pageable) {
        return productRepository.findByStatus(status, pageable);
    }
    
    /**
     * دریافت محصولات بر اساس وضعیت فعالیت
     */
    public Page<Product> getProductsByActiveStatus(Boolean isActive, Pageable pageable) {
        return productRepository.findByIsActive(isActive, pageable);
    }
    
    /**
     * دریافت محصولات ویژه با pagination
     */
    public Page<Product> getFeaturedProducts(Boolean isFeatured, Pageable pageable) {
        return productRepository.findByIsFeatured(isFeatured, pageable);
    }
    
    /**
     * شمارش کل محصولات
     */
    public long count() {
        return productRepository.count();
    }
    
    /**
     * شمارش محصولات بر اساس وضعیت فعالیت
     */
    public long countByActiveStatus(Boolean isActive) {
        return productRepository.countByIsActive(isActive);
    }
    
    /**
     * شمارش محصولات بر اساس وضعیت
     */
    public long countByStatus(ProductStatus status) {
        return productRepository.countByStatus(status);
    }
    
    /**
     * شمارش محصولات ویژه
     */
    public long countByFeaturedStatus(Boolean isFeatured) {
        return productRepository.countByIsFeatured(isFeatured);
    }
}
