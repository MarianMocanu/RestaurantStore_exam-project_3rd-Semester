package dk.kea.stud.fourplayers.restaurantstore.product;

import dk.kea.stud.fourplayers.restaurantstore.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "categories")
public class Category extends BaseEntity {
  @Column(name = "name")
  private String name;

  public Category() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
