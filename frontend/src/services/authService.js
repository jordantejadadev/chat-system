import api from "../api/axios";

export async function login(loginRequest) {

    const response = await api.post(
        "/auth/login",
        loginRequest
    );

    return response.data;
}

export async function getCurrentUser() {

    const response = await api.get("/users/me");

    return response.data;
}