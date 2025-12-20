package com.ssafy.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.admin.dto.OpenRequest;
import com.ssafy.admin.dto.OpenRequestStats;

@Mapper
public interface AdminMapper {
	List<OpenRequest> getOpenRequests(@Param("offset") int offset,
            @Param("size") int size,
            @Param("type") String type) throws Exception;

	long countOpenRequests(@Param("type") String type) throws Exception;
	
	OpenRequestStats getOpenRequestStats() throws Exception;
	public int approveOpenRequests(long reqId) throws Exception;
	public int rejectRequests(long reqId) throws Exception;
}
