import { Avatar } from "@mui/material";
import styles from "./CustomAvatar.module.scss"
import { FC, useState } from "react";
import { MenuAvatar } from "../menuAvatar/MenuAvatar";

type CustomAvatarProps = {
  width?: string,
  height?: string,
  name: string
}

export const CustomAvatar: FC<CustomAvatarProps> = ({width = 40, height = 40, name}) => {
  const [isActive, setIsActive] = useState(false);
  const handleAvatar = () => {
    setIsActive(!isActive)
  }
  return (<div>
    <Avatar
    className={styles.customAvatarWrapper}
      alt={name}
      onClick={handleAvatar}
      src="/static/images/avatar/1.jpg"
      sx={{ width, height }}
    />
    {isActive && <MenuAvatar />}
  </div>);
}