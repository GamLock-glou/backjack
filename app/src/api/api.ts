import * as axios from 'axios'

const instance = axios.create({
  baseURL: "http://localhost:4000",
  headers: {
    Accept: 'application/json',
    Authorization: `XXX-TOKEN ${localStorage.getItem("token")}`,
  },
})

export const queries = {
    async me() {
        const token = localStorage.getItem("token")
        console.log(token)
    },
    async signin(name: string, password: string) {
        const data = instance.post("/signin", {name, password})
        console.log(data)
    }
}