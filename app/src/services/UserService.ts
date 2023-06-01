import {createApi, fetchBaseQuery} from "@reduxjs/toolkit/dist/query/react"
import { IUser, IUserBody } from "../models/IUser"

export const userService = createApi({
    reducerPath: 'userService',
    baseQuery: fetchBaseQuery({
        baseUrl: 'http://localhost:4000',
    }),
    tagTypes: ['User'],
    endpoints: (build) => ({
        authUserWithToken: build.query<IUser, string>({
            query: () => ({
                url: '/signin',
                headers: {
                    'content-type': 'text/plain',
                    'XXX-TOKEN': localStorage.getItem('token') || ""
                }
            }),
        }),
        authUser: build.mutation<IUser, IUserBody>({
            query: (payload) => ({
                url: '/signin',
                method: 'POST',
                body: payload
            }),
        })
    })
})

export const {useAuthUserMutation, useAuthUserWithTokenQuery} = userService
