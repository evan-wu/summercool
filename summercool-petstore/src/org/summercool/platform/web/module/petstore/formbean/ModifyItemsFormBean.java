package org.summercool.platform.web.module.petstore.formbean;

import java.util.ArrayList;
import java.util.List;

import org.summercool.platform.web.module.petstore.pojo.Pet;

public class ModifyItemsFormBean {

	private List<Pet> pets = new ArrayList<Pet>();

	public List<Pet> getPets() {
		return pets;
	}

	public void setPets(List<Pet> pets) {
		this.pets = pets;
	}

}
