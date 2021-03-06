import axios from "axios";
import inMemoryJwt from "./inMemoryJwtService";

const REVIEW_BASED_REST_API_URL = "https://airbnbexperiences-springboot.herokuapp.com/api/review";


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
        let body = "body"
        return await axios.put(REVIEW_BASED_REST_API_URL + "/approveReview/" + id, body,
        {
            headers: {
                'Authorization':`Bearer ${inMemoryJwt.getToken()}`
            } 
        })
    }

    async denyReview(id) {
        let body = "body"
        return await axios.put(REVIEW_BASED_REST_API_URL + "/denyReview/" + id, body,
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

    getBookableReviews(id) {
        return axios.get(REVIEW_BASED_REST_API_URL + "/getBookableReviews/" + id)
    }
}


export default new ReviewService()