package dk.kea.stud.fourplayers.restaurantstore.controllers;

import dk.kea.stud.fourplayers.restaurantstore.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ProductController {
  private final CategoryRepository categories;
  private final ProductRepository products;
  private final String ADD_OR_UPDATE_PRODUCT = "products/addOrUpdateProduct";

  public ProductController(CategoryRepository categoryRepo, ProductRepository productRepo) {
    this.categories = categoryRepo;
    this.products = productRepo;
  }

  @ModelAttribute("allCategories")
  public List<Category> populateCategories() {
    return categories.findAll();
  }

  @GetMapping("/list")
  @ResponseBody
  public List<Product> testProducts() {
    return products.findAll();
  }

  @GetMapping("/add")
  public String addProduct(Model model) {
    model.addAttribute("unitPrice", new Price());
    model.addAttribute("product", new Product());

    return ADD_OR_UPDATE_PRODUCT;
  }

  @PostMapping("/add")
  public String saveProduct(Product product, BindingResult result, Model model) {
    if (result.hasErrors()) {
      model.addAttribute("product", product);
      return ADD_OR_UPDATE_PRODUCT;
    } else {
      products.save(product);
      return "redirect:/list";
    }
  }
}
