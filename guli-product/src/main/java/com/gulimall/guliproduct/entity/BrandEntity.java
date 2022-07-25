package com.gulimall.guliproduct.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.common.validator.ZeroOrOneValue;
import com.common.validator.group.AddGroup;
import com.common.validator.group.UpdateGroup;
import com.common.validator.group.UpdateStatus;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author duh
 * @email hdu_mail@126.com
 * @date 2022-05-05 22:42:49
 * entity添加校验注解，实现后端的数据校验
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	@NotNull(groups = {UpdateGroup.class, UpdateStatus.class})
	@Null(groups = AddGroup.class)
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotEmpty(message = "品牌名不能为空",groups = {UpdateGroup.class,AddGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotEmpty(groups = {UpdateGroup.class,AddGroup.class})
	@URL(message = "必须是合法地址",groups = {UpdateGroup.class,AddGroup.class})
	private String logo;
	/**
	 * 介绍
	 */

	@NotEmpty(groups = {UpdateGroup.class,AddGroup.class})
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@ZeroOrOneValue(vals = {0,1},groups = {AddGroup.class,UpdateGroup.class,UpdateStatus.class}) //校验传入的值必须之value里指定的
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotNull(groups = {UpdateGroup.class,AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$",groups = {UpdateGroup.class,AddGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(groups = {UpdateGroup.class,AddGroup.class})
	@Min(value = 0,message = "sort必须大于等于0",groups = {UpdateGroup.class,AddGroup.class})
	private Integer sort;

}
