package com.ssafy.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsertMenuTagMapping {
	long menuId;
	long tagId;
}
