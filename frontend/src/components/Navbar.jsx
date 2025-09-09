import { useContext, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import AuthContext from "../context/AuthContext";

const Navbar = () => {
  const { user, logout } = useContext(AuthContext);
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <div className="nav-container">
        <Link to="/" className="logo">
          CarRental
        </Link>

        <div className="hamburger" onClick={() => setMenuOpen(!menuOpen)}>
          ☰
        </div>

        <ul className={`nav-links ${menuOpen ? "open" : ""}`}>
        {user?.role === "CUSTOMER" && <li><Link to="/">Home</Link></li>}
          {user?.role === "MANAGER" && <li><Link to="/cars">Home</Link></li>}
          {user?.role === "MANAGER" && <li><Link to="/accounts">Accounts</Link></li>}
          {user?.role === "CUSTOMER" &&   <li><Link to="/rental-history">My Rentals</Link></li>}
          {user?.role === "MANAGER" && <li><Link to="/adminpayments">Payments</Link></li>}
          {user?.role === "CUSTOMER" &&   <li><Link to="/payments">Payments</Link></li>}

          {user ? (
            <li><button className="btn logout-btn" onClick={handleLogout}>Logout</button></li>
          ) : (
            <>
              <li><Link to="/login">Login</Link></li>
              <li><Link to="/register">Register</Link></li>
            </>
          )}




        </ul>
      </div>
    </nav>
  );
};

export default Navbar;
