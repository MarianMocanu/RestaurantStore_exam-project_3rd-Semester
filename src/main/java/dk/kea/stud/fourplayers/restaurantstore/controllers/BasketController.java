package dk.kea.stud.fourplayers.restaurantstore.controllers;

import dk.kea.stud.fourplayers.restaurantstore.product.Product;
import dk.kea.stud.fourplayers.restaurantstore.product.ProductRepository;
import dk.kea.stud.fourplayers.restaurantstore.order.Basket;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@SessionAttributes("basket")
public class BasketController {

  private final ProductRepository products;

  public BasketController(ProductRepository products) {
    this.products = products;
  }

  @PostMapping("/addToBasket")
  public String addToBasket(@SessionAttribute("basket") Basket theBasket,
                            @RequestParam(required = false, name = "quantity") Integer quantity,
                            @RequestParam(name = "productId") Integer productId) {
    Optional<Product> product = products.findById(productId);
    if (product.isPresent()) {
      if (quantity == null) {
        quantity = 1;
      }

      theBasket.addProduct(productId, quantity);
    }
    return "redirect:/shop";
  }

  @GetMapping("/basket")
  public String seeBasket(@SessionAttribute("basket") Basket basket, Model model) {
    Map<Integer, Product> productMap = new HashMap<>();
    for (Integer id : basket.getProductsInBasket().keySet()) {
      Optional<Product> product = products.findById(id);
      if (product.isPresent()) {
        productMap.put(id, product.get());
      }
    }
    double total = 0;
    for (Map.Entry<Integer, Integer> entry : basket.getProductsInBasket().entrySet()) {
      total += productMap.get(entry.getKey()).getBestPriceForQuantity(entry.getValue()) * entry.getValue();
    }
    model.addAttribute("productMap", productMap);
    model.addAttribute("basket", basket);
    model.addAttribute("total", total);
    return "order/viewAndUpdateBasket";
  }

  @PostMapping("/basket")
  public String updateBasket(@SessionAttribute("basket") Basket basket,
                             @ModelAttribute Basket submittedBasket, RedirectAttributes redir) {
    for (Integer quantity: submittedBasket.getProductsInBasket().values()) {
      if (quantity < 1) {
        redir.addFlashAttribute("error", "All quantities must be greater than 0");
        return "redirect:/basket";
      }
    }
    basket.setProductsInBasket(submittedBasket.getProductsInBasket());
    return "redirect:/basket";
  }

  @GetMapping("/basket/delete/{id}")
  public String removeProduct(@SessionAttribute("basket") Basket basket,
                              @PathVariable(name = "id") Integer itemId) {
    basket.removeProduct(itemId);
    return "redirect:/basket";
  }
}
