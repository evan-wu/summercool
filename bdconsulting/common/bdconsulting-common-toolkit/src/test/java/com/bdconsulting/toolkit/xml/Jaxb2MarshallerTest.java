/**
 * 
 */
package com.bdconsulting.toolkit.xml;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Datetime   ： 2011-5-5 下午01:38:27<br>
 * Title      :  Jaxb2MarshallerTest.java<br>
 * Description:   <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class Jaxb2MarshallerTest {

	public static void main(String[] args) {
		testMarshal();
		testUnmarshal();
	}

	private static void testMarshal() {
		ItemVO item = new ItemVO();
		item.setId(1L);
		item.setSubject("测试产品");
		item.setPrice(new BigDecimal("100.50"));

		System.out.println(Jaxb2Marshaller.marshal(item, ItemVO.class));
	}

	private static void testUnmarshal() {
		String xml = "<?xml version='1.0' encoding='UTF-8'?><Item id='1'><Subject>测试产品</Subject><Price>100.50</Price></Item>";

		System.out.println(Jaxb2Marshaller.unmarshal(xml, ItemVO.class));
	}

}

@XmlRootElement(name = "Item")
@XmlAccessorType(XmlAccessType.FIELD)
class ItemVO implements Serializable {

	private static final long serialVersionUID = 8494187361455869445L;

	@XmlAttribute
	private Long id;

	@XmlElement(name = "Subject")
	private String subject;

	@XmlElement(name = "Price")
	private BigDecimal price;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
