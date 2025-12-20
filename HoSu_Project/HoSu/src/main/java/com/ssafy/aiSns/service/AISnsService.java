package com.ssafy.aiSns.service;

import java.util.List;

import com.ssafy.sns.dto.SnsBoardResp;

public interface AISnsService {
	public List<SnsBoardResp> getTop5BoardIds(String query) throws Exception;
}
