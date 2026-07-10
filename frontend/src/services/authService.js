import api from "../api/axios";

export async function login(loginRequest) {

    const response = await api.post(
        "/auth/login",
        loginRequest
    );

    return response.data;
}