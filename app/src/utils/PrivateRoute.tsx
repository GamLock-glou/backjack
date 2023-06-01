import { FC } from "react"
import { Outlet, Navigate } from "react-router-dom"

type PrivateRouteProps = {
  isAuth: boolean
  redirectPath?: string
}

export const PrivateRoute: FC<PrivateRouteProps> = ({isAuth, redirectPath='/login'}) => {
  if (!isAuth) {
    return <Navigate to={redirectPath} replace />;
  }

  return <Outlet />;
}