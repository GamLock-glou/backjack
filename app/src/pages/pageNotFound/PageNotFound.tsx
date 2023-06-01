import { Link } from "react-router-dom";

export function PageNotFound() {
  return (<div>
    <div>404 error. Page Not Found</div>
    <Link to='/'>Go to Home Page</Link>
  </div>);
}
