package com.webrayan.store.modules.admin.controller;

import com.webrayan.store.modules.catalog.entity.Product;
import com.webrayan.store.modules.catalog.entity.Category;
import com.webrayan.store.modules.catalog.enums.ProductStatus;
import com.webrayan.store.modules.catalog.service.ProductService;
import com.webrayan.store.modules.catalog.service.CategoryService;
import com.webrayan.store.modules.acl.entity.User;
import com.webrayan.store.modules.acl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;

    /**
     * Exclude images field from automatic binding to prevent conflicts
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("images");
    }

    /**
     * نمایش لیست محصولات با فیلتر و جستجو
     */
    @GetMapping
    @Transactional(readOnly = true)
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Boolean isFeatured,
            Model model) {

        // تنظیم صفحه‌بندی و مرتب‌سازی
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? 
                           Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products;

        // اعمال فیلترها
        if (search != null && !search.trim().isEmpty()) {
            if (status != null) {
                products = productService.searchProductsByNameAndStatus(search, status, pageable);
            } else {
                products = productService.searchProductsByName(search, pageable);
            }
        } else if (status != null) {
            products = productService.getProductsByStatus(status, pageable);
        } else if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId)
                    .orElseThrow(() -> new RuntimeException("دسته‌بندی یافت نشد"));
            products = productService.getProductsByCategory(category, pageable);
        } else if (isActive != null) {
            products = productService.getProductsByActiveStatus(isActive, pageable);
        } else if (isFeatured != null) {
            products = productService.getFeaturedProducts(isFeatured, pageable);
        } else {
            products = productService.getAllProducts(pageable);
        }

        // آمار کلی
        long totalProducts = productService.count();
        long activeProducts = productService.countByActiveStatus(true);
        long publishedProducts = productService.countByStatus(ProductStatus.PUBLISHED);
        long draftProducts = productService.countByStatus(ProductStatus.DRAFT);

        // دسته‌بندی‌ها برای فیلتر
        var categories = categoryService.getActiveCategories();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("statuses", ProductStatus.values());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("search", search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedIsActive", isActive);
        model.addAttribute("selectedIsFeatured", isFeatured);
        
        // آمار
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("activeProducts", activeProducts);
        model.addAttribute("publishedProducts", publishedProducts);
        model.addAttribute("draftProducts", draftProducts);

        return "admin/pages/products";
    }

    /**
     * نمایش جزئیات محصول
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductByIdWithImages(id)
                .orElseThrow(() -> new RuntimeException("محصول یافت نشد"));
        
        model.addAttribute("product", product);
        return "admin/pages/product-detail";
    }

    /**
     * نمایش فرم ایجاد محصول جدید
     */
    @GetMapping("/create")
    public String createProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getActiveCategories());
        model.addAttribute("sellers", userService.getAllUsers()); // همه کاربران که می‌توانند فروشنده باشند
        model.addAttribute("statuses", ProductStatus.values());
        return "admin/pages/product-create";
    }

    /**
     * ایجاد محصول جدید
     */
    @PostMapping("/create")
    public String createProduct(@ModelAttribute Product product,
                               @RequestParam Long sellerId,
                               @RequestParam Long categoryId,
                               @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
                               RedirectAttributes redirectAttributes) {
        try {
            // تنظیم فروشنده و دسته‌بندی
            User seller = userService.findById(sellerId);
            Category category = categoryService.getCategoryById(categoryId)
                    .orElseThrow(() -> new RuntimeException("دسته‌بندی یافت نشد"));
            
            product.setSeller(seller);
            product.setCategory(category);
            
            // Clear any existing images from form binding issues
            product.setImages(new ArrayList<>());
            
            Product savedProduct = productService.createProduct(product);
            
            // Handle image uploads if provided
            if (imageFiles != null && imageFiles.length > 0) {
                for (MultipartFile file : imageFiles) {
                    if (!file.isEmpty()) {
                        // TODO: Implement image processing and saving
                        // This would typically involve:
                        // 1. Validating file type and size
                        // 2. Saving file to storage (disk/cloud)
                        // 3. Creating ProductImage entity
                        // 4. Associating with product
                    }
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "محصول با موفقیت ایجاد شد");
            return "redirect:/admin/products/" + savedProduct.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "خطا در ایجاد محصول: " + e.getMessage());
            return "redirect:/admin/products/create";
        }
    }

    /**
     * نمایش فرم ویرایش محصول
     */
    @GetMapping("/edit")
    public String editProductForm(@RequestParam Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("محصول یافت نشد"));
        
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getActiveCategories());
        model.addAttribute("sellers", userService.getAllUsers());
        model.addAttribute("statuses", ProductStatus.values());
        return "admin/pages/product-edit";
    }

    /**
     * ویرایش محصول
     */
    @PostMapping("/edit")
    public String editProduct(@ModelAttribute Product product,
                             @RequestParam Long categoryId,
                             @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
                             RedirectAttributes redirectAttributes) {
        try {
            // تنظیم فروشنده و دسته‌بندی
            Category category = categoryService.getCategoryById(categoryId)
                    .orElseThrow(() -> new RuntimeException("دسته‌بندی یافت نشد"));
            
            product.setCategory(category);
            
            // Clear any existing images from form binding issues
            product.setImages(new ArrayList<>());
            
            Product updatedProduct = productService.updateProduct(product.getId(), product);
            
            // Handle new image uploads if provided
            if (imageFiles != null && imageFiles.length > 0) {
                for (MultipartFile file : imageFiles) {
                    if (!file.isEmpty()) {
                        // TODO: Implement image processing and saving for updates
                    }
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "محصول با موفقیت به‌روزرسانی شد");
            return "redirect:/admin/products/" + updatedProduct.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "خطا در به‌روزرسانی محصول: " + e.getMessage());
            return "redirect:/admin/products/edit?id=" + product.getId();
        }
    }

    /**
     * حذف محصول
     */
    @PostMapping("/delete")
    public String deleteProduct(@RequestParam Long id, 
                               RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "محصول با موفقیت حذف شد");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "خطا در حذف محصول: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    /**
     * تغییر وضعیت محصول
     */
    @PostMapping("/update-status")
    public String updateProductStatus(@RequestParam Long id, 
                                     @RequestParam ProductStatus status,
                                     RedirectAttributes redirectAttributes) {
        try {
            productService.updateProductStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", 
                "وضعیت محصول با موفقیت به‌روزرسانی شد");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "خطا در به‌روزرسانی وضعیت: " + e.getMessage());
        }
        return "redirect:/admin/products/" + id;
    }

    /**
     * تغییر وضعیت فعال/غیرفعال محصول
     */
    @PostMapping("/toggle-active")
    public String toggleProductActive(@RequestParam Long id, 
                                     RedirectAttributes redirectAttributes) {
        try {
            productService.toggleProductStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "وضعیت فعالیت محصول تغییر کرد");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "خطا در تغییر وضعیت: " + e.getMessage());
        }
        return "redirect:/admin/products/" + id;
    }

    /**
     * به‌روزرسانی موجودی محصول
     */
    @PostMapping("/update-stock")
    public String updateProductStock(@RequestParam Long id, 
                                    @RequestParam Integer quantity,
                                    RedirectAttributes redirectAttributes) {
        try {
            productService.updateStock(id, quantity);
            redirectAttributes.addFlashAttribute("successMessage", 
                "موجودی محصول به‌روزرسانی شد");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "خطا در به‌روزرسانی موجودی: " + e.getMessage());
        }
        return "redirect:/admin/products/" + id;
    }
}
