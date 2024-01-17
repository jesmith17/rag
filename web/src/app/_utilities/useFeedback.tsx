import axios from "axios"

const url = 'http://ec2-3-87-113-31.compute-1.amazonaws.com:8080/feedback'

type feedbackParams = {
    thumbsUp: boolean
    justification: {
        complete: boolean
    }
}

type feedbackResponse = {
    message: String
}
const useFeedback = async (id:String, feedbackParams:feedbackParams): Promise<feedbackResponse> => {

    const result = await axios.post(
        `${url}/${id}`,
       feedbackParams
    )

    console.log(result)

    return {
        message: 'Thank you for your feedback'
    }
}

export default useFeedback