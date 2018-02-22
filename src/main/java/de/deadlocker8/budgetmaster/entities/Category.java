package de.deadlocker8.budgetmaster.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Category
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer ID;
	private String name;
	private String color;

	public Integer getID() {
		return ID;
	}

	public void setId(Integer ID) {
		this.ID = ID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public String toString()
	{
		return "Category [ID=" + ID + ", name=" + name + ", color=" + color + "]";
	}
}