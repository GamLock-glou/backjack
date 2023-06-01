import { NavLink, useLocation, useNavigate } from "react-router-dom";
import { useAppSelector } from "../../hooks/redux";
import styles from "./NavBar.module.scss"
import { Button } from "@mui/material";
import { CustomAvatar } from "./avatar/CustomAvatar";
import cn from "classnames";

const urlArray = ['/login']
type classNameFuncProps = {
  isActive: boolean;
  isPending: boolean;
}

const classNameFunc = ({ isActive }: classNameFuncProps) => (isActive ? cn(styles.activeLinkStyle, styles.linkStyle) : styles.linkStyle);

export function NavBar() {
  const location = useLocation();
  const navigate = useNavigate()
  const {isAuth, user} = useAppSelector(_ => _.UserReducer)
  const isUrlDisableNavBar = urlArray.some(url => url === location.pathname)
  if(isUrlDisableNavBar) {
    return null
  }

  const handleSignIn = () => {
    navigate("/login")
  }

  return (<div className={styles.navBarContainer}>
    {
      isAuth && user ? 
        <div className={styles.navBarIsAuthConatainer}>
          <div className={styles.linkContainer}>
            <NavLink className={classNameFunc} to="/">Home</NavLink>
            <NavLink className={classNameFunc} to="/lobbies">Lobbies</NavLink>
          </div>

          <CustomAvatar name={user.name} />
        </div> :       
        <Button onClick={handleSignIn} variant="outlined">Sign In</Button>
    }
  </div>);
}