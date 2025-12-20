package com.ssafy.aiSns.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.aiSns.dto.AiExtractResult;
import com.ssafy.sns.dto.SnsBoardImage;
import com.ssafy.sns.dto.SnsBoardResp;

@Mapper
public interface AISnsMapper {
	public List<Long> findTop5BoardIdsByCond(AiExtractResult cond) throws Exception;

	public Long findSidoNoByName(String sidoName) throws Exception;

	public Long findGunguCodeByName(Long sidoNo, String gunguName) throws Exception;

	public List<SnsBoardResp> selectBoardsByIds(@Param("boardIds")List<Long> boardIds) throws Exception;

	public List<SnsBoardImage> selectImagesByBoardId(@Param("boardId")Long boardId) throws Exception;
}
