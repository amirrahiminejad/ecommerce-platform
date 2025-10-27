package com.webrayan.store.modules.admin.controller;

import com.webrayan.store.modules.catalog.entity.Category;
import com.webrayan.store.modules.catalog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * کنترلر مدیریت دسته‌بندی‌های کالا در پنل ادمین
 */
@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    /**
     * نمایش لیست دسته‌بندی‌ها با فیلتر و جستجو
     */
    @GetMapping
    @Transactional(readOnly = true)
    public String listCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Long parentId,
            Model model) {

        // تنظیم صفحه‌بندی و مرتب‌سازی
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? 
                           Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Category> categories;

        // اعمال فیلترها
        if (search != null && !search.trim().isEmpty()) {
            categories = categoryService.searchCategoriesByName(search, pageable);
        } else if (isActive != null) {
            categories = categoryService.getCategoriesByActiveStatus(isActive, pageable);
        } else if (parentId != null) {
            if (parentId == 0) {
                categories = categoryService.getTopLevelCategories(pageable);
            } else {
                Category parent = categoryService.getCategoryById(parentId)
                        .orElseThrow(() -> new RuntimeException("دسته‌بندی والد یافت نشد"));
                categories = categoryService.getSubCategories(parent, pageable);
            }
        } else {
            categories = categoryService.getAllCategories(pageable);
        }

        // آمار کلی
        long totalCategories = categoryService.count();
        long activeCategories = categoryService.countByActiveStatus(true);
        long topLevelCategories = categoryService.countTopLevelCategories();

        // دسته‌بندی‌های والد برای فیلتر
        var parentCategories = categoryService.getTopLevelCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("parentCategories", parentCategories);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("search", search);
        model.addAttribute("selectedIsActive", isActive);
        model.addAttribute("selectedParentId", parentId);
        
        // آمار
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("activeCategories", activeCategories);
        model.addAttribute("topLevelCategories", topLevelCategories);

        return "admin/pages/categories";
    }

    /**
     * نمایش جزئیات دسته‌بندی
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public String viewCategory(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryByIdWithSubCategories(id)
                .orElseThrow(() -> new RuntimeException("دسته‌بندی یافت نشد"));
        
        model.addAttribute("category", category);
        return "admin/pages/category-detail";
    }

    /**
     * نمایش فرم ایجاد دسته‌بندی جدید
     */
    @GetMapping("/create")
    public String createCategoryForm(@RequestParam(required = false) Long parentId, Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("parentCategories", categoryService.getActiveCategories());
        model.addAttribute("selectedParentId", parentId);
        return "admin/pages/category-create";
    }

    /**
     * ذخیره دسته‌بندی جدید
     */
    @PostMapping("/create")
    public String createCategory(@ModelAttribute Category category, 
                                @RequestParam(required = false) Long parentId,
                                RedirectAttributes redirectAttributes) {
        try {
            if (parentId != null && parentId > 0) {
                Category parent = categoryService.getCategoryById(parentId)
                        .orElseThrow(() -> new RuntimeException("دسته‌بندی والد یافت نشد"));
                category.setParent(parent);
            }

            categoryService.createCategory(category);
            redirectAttributes.addFlashAttribute("successMessage", "دسته‌بندی با موفقیت ایجاد شد");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "خطا در ایجاد دسته‌بندی: " + e.getMessage());
            return "redirect:/admin/categories/create";
        }
    }

    /**
     * نمایش فرم ویرایش دسته‌بندی
     */
    @GetMapping("/edit")
    public String editCategoryForm(@RequestParam Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("دسته‌بندی یافت نشد"));
        
        model.addAttribute("category", category);
        model.addAttribute("parentCategories", categoryService.getActiveCategories());
        return "admin/pages/category-edit";
    }

    /**
     * به‌روزرسانی دسته‌بندی
     */
    @PostMapping("/edit")
    public String updateCategory(@ModelAttribute Category category,
                                @RequestParam Long id,
                                @RequestParam(required = false) Long parentId,
                                RedirectAttributes redirectAttributes) {
        try {
            if (parentId != null && parentId > 0) {
                Category parent = categoryService.getCategoryById(parentId)
                        .orElseThrow(() -> new RuntimeException("دسته‌بندی والد یافت نشد"));
                category.setParent(parent);
            } else {
                category.setParent(null);
            }

            categoryService.updateCategory(id, category);
            redirectAttributes.addFlashAttribute("successMessage", "دسته‌بندی با موفقیت به‌روزرسانی شد");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "خطا در به‌روزرسانی دسته‌بندی: " + e.getMessage());
            return "redirect:/admin/categories/edit?id=" + id;
        }
    }

    /**
     * حذف دسته‌بندی
     */
    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "دسته‌بندی با موفقیت حذف شد");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "خطا در حذف دسته‌بندی: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    /**
     * تغییر وضعیت فعالیت دسته‌بندی
     */
    @PostMapping("/toggle-status/{id}")
    public String toggleCategoryStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.toggleCategoryStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "وضعیت دسته‌بندی با موفقیت تغییر کرد");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "خطا در تغییر وضعیت: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}
