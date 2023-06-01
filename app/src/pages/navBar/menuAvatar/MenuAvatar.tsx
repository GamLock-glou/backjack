import { Button } from "@mui/material";
import styles from "./MenuAvatar.module.scss"
import { useAppDispatch, useAppSelector } from "../../../hooks/redux";
import { signOut } from "../../../store/reducers/UserSlice";

export function MenuAvatar() {
  const {user} = useAppSelector(_ => _.UserReducer);
  const dispatch = useAppDispatch()
  const handleSignOut = () => {
    dispatch(signOut())
  }
  return (<div className={styles.menuAvatarContainer}>
    <div>
      Name: {user?.name}
    </div>
    <div>
      Balance: ${user?.money}
    </div>    
    <Button
      className={styles.buttonStyle}
      onClick={handleSignOut}
      variant="outlined"
    >
      Sign Out
    </Button>
  </div>);
}
