package org.summercool.platform.web.module.petstore.controllers.item;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.summercool.platform.web.module.petstore.formbean.ModifyItemsFormBean;
import org.summercool.platform.web.module.petstore.pojo.Pet;

@SuppressWarnings("deprecation")
public class ModifyItemsController extends SimpleFormController {

	public ModifyItemsController() {
		setBindOnNewForm(true);
		setCommandName("modifyItemsFormBean");
		setCommandClass(ModifyItemsFormBean.class);
		setFormView("/petstore/views/item/modifyItems");
	}

	@Override
	protected void onBindOnNewForm(HttpServletRequest request, Object command) throws Exception {
		ModifyItemsFormBean formBean = (ModifyItemsFormBean) command;
		//
		Pet pet1 = new Pet();
		pet1.setName("王");
		pet1.setAge("25");

		Pet pet2 = new Pet();
		pet2.setName("少");
		pet2.setAge("26");

		Pet pet3 = new Pet();
		pet3.setName("川");
		pet3.setAge("27");
		//
		formBean.getPets().add(pet1);
		formBean.getPets().add(pet2);
		formBean.getPets().add(pet3);
	}

	@Override
	protected Map<?, ?> referenceData(HttpServletRequest request) throws Exception {
		return super.referenceData(request);
	}

	@Override
	protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {
		ModifyItemsFormBean formBean = (ModifyItemsFormBean) command;
		for (int i = 0; i < formBean.getPets().size(); i++) {
			Pet pet = formBean.getPets().get(i);
			if (!"25".equals(pet.getAge())) {
				errors.reject("" + i, "年龄必须为25!");
			}
		}
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
			BindException errors) throws Exception {
		return showForm(request, response, errors);
	}

}
