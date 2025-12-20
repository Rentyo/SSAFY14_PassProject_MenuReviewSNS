package com.ssafy.admin.service;

import java.util.List;

import com.ssafy.admin.dto.OpenRequest;
import com.ssafy.admin.dto.OpenRequestPage;

public interface AdminService {
	public OpenRequestPage getOpenRequests(int page, int size, String type) throws Exception;
	public int approveOpenRequests(long reqId) throws Exception;
	public int rejectRequests(long reqId) throws Exception;
}
