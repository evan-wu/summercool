/**
 * 
 */
package com.bdconsulting.toolkit.xml;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
		item.setPrice(new PriceVO(new BigDecimal("100.50"), "CNY"));
		item.setImage(new ImagesVO(Arrays.asList("a.jpg", "b.jpg", "c.jpg")));

		System.out.println(Jaxb2Marshaller.marshal(item, ItemVO.class));
	}

	private static void testUnmarshal() {
		String xml = "<?xml version='1.0' encoding='UTF-8'?><Item id='1'><Subject>测试产品</Subject><Price currency='CNY'><Value>100.50</Value></Price><Images><Image>a.jpg</Image><Image>b.jpg</Image><Image>c.jpg</Image></Images></Item>";

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
	private PriceVO price;

	@XmlElement(name = "Images")
	private ImagesVO image;

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

	public PriceVO getPrice() {
		return price;
	}

	public void setPrice(PriceVO price) {
		this.price = price;
	}

	public ImagesVO getImage() {
		return image;
	}

	public void setImage(ImagesVO image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
class ImagesVO implements Serializable {

	private static final long serialVersionUID = 3367588697955244944L;

	public ImagesVO() {
	}

	public ImagesVO(List<String> images) {
		this.images = images;
	}

	@XmlElement(name = "Image")
	private List<String> images;

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
class PriceVO implements Serializable {

	private static final long serialVersionUID = -5292463671697234547L;

	public PriceVO() {
	}

	public PriceVO(BigDecimal value, String currency) {
		this.value = value;
		this.currency = currency;
	}

	@XmlElement(name = "Value")
	private BigDecimal value;

	@XmlAttribute
	private String currency;

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
