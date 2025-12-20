package com.ssafy.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssafy.admin.dto.OpenRequest;
import com.ssafy.admin.dto.OpenRequestPage;
import com.ssafy.admin.dto.OpenRequestStats;
import com.ssafy.admin.mapper.AdminMapper;

@Service("AdminServiceImpl")
public class AdminServiceImpl implements AdminService {
	
	@Autowired
	private AdminMapper mapper;
	
	@Override
	public OpenRequestPage getOpenRequests(int page, int size, String type) throws Exception {
		int offset = page * size;

	    List<OpenRequest> content = mapper.getOpenRequests(offset, size, type);
	    long total = mapper.countOpenRequests(type);

	    OpenRequestStats stats = mapper.getOpenRequestStats();

	    OpenRequestPage result = new OpenRequestPage();
	    result.setContent(content);
	    result.setPage(page);
	    result.setSize(size);
	    result.setTotal(total);
	    result.setTotalPages((int) Math.ceil((double) total / size));

	    result.setAllCount(stats.getAllCount());
	    result.setPenCount(stats.getPenCount());
	    result.setAppCount(stats.getAppCount());

	    return result;
	}

	@Override
	public int approveOpenRequests(long reqId) throws Exception {
		return mapper.approveOpenRequests(reqId);
	}

	@Override
	public int rejectRequests(long reqId) throws Exception {
		return mapper.rejectRequests(reqId);
	}

}
