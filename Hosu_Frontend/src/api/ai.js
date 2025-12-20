import api from './index'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

/**
 * AI 자연어 검색 파싱 API
 * @param {string} query - 사용자 자연어 입력
 * @returns {Promise} 파싱된 검색 조건
 */
export const parseAIQuery = async (query) => {
    const response = await api.post(`/ai/parse-query`, {
        query: query
    })
    return response.data
}
