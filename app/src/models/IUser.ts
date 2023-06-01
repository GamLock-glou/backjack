export interface IUser {
    id: number,
    name: string,
    money: number,
    token: string
}

export interface IUserBody {
    name: string,
    password: string
}