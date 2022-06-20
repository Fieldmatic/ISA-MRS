import axios from "axios";
import inMemoryJwt from "./inMemoryJwtService";

const REVIEW_BASED_REST_API_URL = "http://localhost:8081/api/review";


class ReviewService {
    addReview(data){
        return axios.post(REVIEW_BASED_REST_API_URL + "/addReview", data,
        {
            headers: {
                'Authorization':`Bearer ${inMemoryJwt.getToken()}`
            } 
        })
    }

    async approveReview(id) {
        return await axios.post(REVIEW_BASED_REST_API_URL + "/approveReview/" + id,
        {
            headers: {
                'Authorization':`Bearer ${inMemoryJwt.getToken()}`
            } 
        })
    }

    async denyReview(id) {
        return await axios.post(REVIEW_BASED_REST_API_URL + "/denyReview/" + id,
        {
            headers: {
                'Authorization':`Bearer ${inMemoryJwt.getToken()}`
            } 
        })
    }

    getAllReviews() {
        return axios.get(REVIEW_BASED_REST_API_URL + "/getAllReviews",
        {
            headers: {
                'Authorization':`Bearer ${inMemoryJwt.getToken()}`
            } 
        })
    }
}


export default new ReviewService()