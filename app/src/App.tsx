import React, { useEffect, useState } from 'react';
import './App.css';
import { Lobbies } from './pages/lobbies/Lobbies';
import { Auth } from './pages/auth/Auth';
import { User } from './type/type';
import { Routes ,Route, useLocation } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from './hooks/redux';
import { PrivateRoute } from './utils/PrivateRoute';
import { PageNotFound } from './pages/pageNotFound/PageNotFound';
import { Home } from './pages/home/Home';
import { NavBar } from './pages/navBar/NavBar';
import { useAuthUserWithTokenQuery } from './services/UserService';
import { setUser } from './store/reducers/UserSlice';
import { Loading } from './pages/loading/Loading';


function App() {
  const {isAuth} = useAppSelector(_ => _.UserReducer)
  const dispatch = useAppDispatch();
  const location = useLocation()
  const {data, isLoading} = useAuthUserWithTokenQuery('');
  // TODO: not good code. Need refactor.
  useEffect(() => {
    const token = localStorage.getItem('token')
    if(token && !isAuth && data) {
      dispatch(setUser(data))
    }
  })
  if(isLoading) {
    return <Loading />
  }
  return (
    <div className="App">
      <NavBar />
      <Routes>
        <Route element={<PrivateRoute isAuth={isAuth} />}>
            <Route path="lobbies" element={<Lobbies />} />
        </Route>
        <Route path="/" element={<Home />} />
        <Route element={<PrivateRoute isAuth={!isAuth} redirectPath='lobbies' />}>
          <Route path="login" element={<Auth />} />
        </Route>
        <Route path="*" element={<PageNotFound />} />

      </Routes>
    </div>
  );
}

export default App;
