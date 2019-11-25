package dk.kea.stud.fourplayers.restaurantstore.controllers;

import dk.kea.stud.fourplayers.restaurantstore.model.*;
import dk.kea.stud.fourplayers.restaurantstore.order.Basket;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@SessionAttributes("basket")
public class ProductController {
  private final CategoryRepository categories;
  private final ProductRepository products;
  private final PriceRepository prices;
  private final ProductImageRepository images;
  private final String ADD_OR_UPDATE_PRODUCT = "products/addOrUpdateProduct";

  public ProductController(CategoryRepository categoryRepo, ProductRepository productRepo,
                           PriceRepository priceRepo, ProductImageRepository imageRepo) {
    this.categories = categoryRepo;
    this.products = productRepo;
    this.prices = priceRepo;
    this.images = imageRepo;
  }

  @ModelAttribute("allCategories")
  public List<Category> populateCategories() {
    return categories.findAll();
  }

  @ModelAttribute("basket")
  public Basket setUpBasket() {
    return new Basket();
  }

  @GetMapping("/list_resp")
  @ResponseBody
  public List<Product> testProducts() {
    return products.findAll();
  }

  @GetMapping(value = {"/shop", "/"})
  public String viewProducts(Model model,
                             @RequestParam(name = "cat", required = false) Integer categoryId,
                             @RequestParam(value = "search", required = false) String input) {
    if (input != null) {
      Collection<Product> productsCollection = new HashSet<>();
      //The \\W+ will match all non-alphabetic characters occurring one or more times
      String[] words = input.split("\\W+");
      for (String word : words) {
        productsCollection.addAll(products.findProductBySearchInput(word));
      }
      if (productsCollection.isEmpty()) {
        model.addAttribute("noProducts", "none");
      } else {
        model.addAttribute("products", productsCollection);
      }
    } else {
      if (categoryId == null) {
        model.addAttribute("products", products.findAll());
      } else {
        model.addAttribute("products", products.findProductsByCategoryId(categoryId));
      }
    }
    return "products/viewProducts";
  }

  @GetMapping("/admin/product/add")
  public String addProduct(Model model) {
    Price newPrice = new Price();
    newPrice.setQuantity(1);
    model.addAttribute("product", new Product());
    model.addAttribute("newPrice", newPrice);
    model.addAttribute("newImage", new ProductImage());

    return ADD_OR_UPDATE_PRODUCT;
  }

  @PostMapping("/admin/product/add")
  public String saveNewProduct(@ModelAttribute Product product, @ModelAttribute Price newPrice,
                               @ModelAttribute ProductImage newImage, BindingResult result, Model model) {
    if (result.hasErrors()) {
      model.addAttribute("product", product);
      model.addAttribute("price", newPrice);
      model.addAttribute("image", newImage);

      return ADD_OR_UPDATE_PRODUCT;
    } else {
      if (newPrice.getQuantity() > 0 && newPrice.getPrice() > 0) {
        product.addPrice(newPrice);
      }
      if (newImage.getUrl() != null && !newImage.getUrl().equals("")) {
        product.addImage(newImage);
      }
      products.save(product);
      return "redirect:/";
    }
  }

  @GetMapping("/admin/product/edit/{productId}")
  public String editProduct(@PathVariable("productId") int productId, Model model) {
    Product product = products.findById(productId).get();
    product.getPrices().sort(Price::compareTo);
    model.addAttribute("product", product);
    model.addAttribute("newPrice", new Price());
    model.addAttribute("newImage", new ProductImage());

    return ADD_OR_UPDATE_PRODUCT;
  }

  @PostMapping("/admin/product/edit/{productId}")
  public String saveEditedProduct(@PathVariable("productId") int productId,
                                  @ModelAttribute Product product, @ModelAttribute Price newPrice,
                                  @ModelAttribute ProductImage newImage, Model model, BindingResult result) {
    if (result.hasErrors()) {
      model.addAttribute("product", product);
      model.addAttribute("price", newPrice);
      model.addAttribute("image", newImage);

      return ADD_OR_UPDATE_PRODUCT;
    } else {
      if (newPrice.getQuantity() > 0 && newPrice.getPrice() > 0) {
        product.addPrice(newPrice);
      }
      if (newImage.getUrl() != null && !newImage.getUrl().equals("")) {
        product.addImage(newImage);
      }
      product.setId(productId);
      products.save(product);

      return "redirect:/";
    }
  }

  @GetMapping("/admin/product/delete/{product_id}")
  public String deleteProduct(@PathVariable(name = "product_id", required = true) int id) {
    products.deleteById(id);
    return "redirect:/";
  }

  @GetMapping("/admin/product/delete/{productId}/price/{priceId}")
  public String deletePrice(@PathVariable("priceId") int priceId,
                            @PathVariable("productId") int productId) {
    prices.deleteById(priceId);
    return "redirect:/admin/product/edit/" + productId;
  }

  @GetMapping("/admin/product/delete/{productId}/image/{imageId}")
  public String deleteImage(@PathVariable("productId") int productId,
                            @PathVariable("imageId") int imageId) {
    images.deleteById(imageId);
    return "redirect:/admin/product/edit/" + productId;
  }

  @GetMapping("/product/{id}")
  public String productDetails(@PathVariable(name = "id") int id, Model model){
    Optional<Product> product = products.findById(id);
    model.addAttribute("product", product.get());
    return "products/productDetail";
  }
}
