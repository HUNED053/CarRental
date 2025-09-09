import { useForm } from "react-hook-form";
import { useContext, useState, useEffect } from "react";
import AuthContext from "../context/AuthContext";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import axios from "../services/api";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import "../App.css";
function parseJwt(token) {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => `%${('00' + c.charCodeAt(0).toString(16)).slice(-2)}`)
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
}

const schema = yup.object().shape({
  email: yup.string().email("Invalid email address").required("Email/Username is required"),
  password: yup.string()
    .min(8, "Use at least 8 characters, including numbers and symbols")
    .required("Password is required"),
});

const Login = () => {
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors, isValid },
    reset,
    setError,
    clearErrors,
    watch
  } = useForm({
    resolver: yupResolver(schema),
    mode: "onChange",
  });

  const onSubmit = async (data) => {
    setLoading(true);
    try {
      const res = await axios.post("/login", data);
      const token = res.data.token;
  
      // Decode the token to get the role
      const decoded = parseJwt(token);
      const role = decoded?.roles?.[0]; // assuming roles is an array
  
      console.log("Decoded role:", role);
  
      login(token); // save token to context/localStorage
  
      toast.success("Login successful!", { autoClose: 3000 });
      reset();
  
      setTimeout(() => {
        if (role?.toLowerCase() === "manager") {
          navigate("/cars");
        } else {
          navigate("/");
        }
      }, 100);
    } catch (error) {
      const errMsg = error.response?.data?.message || "Login failed!";
      if (errMsg.toLowerCase().includes("not found")) {
        setError("email", { type: "manual", message: "Account not found" });
        toast.error("Account not found");
      } else {
        setError("password", { type: "manual", message: "Incorrect email or password" });
        toast.error("Incorrect email or password");
      }
    } finally {
      setLoading(false);
    }
  };
  
  

  useEffect(() => {
    document.querySelector("input[name='email']").focus();
  }, []);

  const togglePassword = () => {
    setShowPassword(prev => !prev);
  };

  return (
    <div className="form-container">
      <div className="form-box">
        <h2>Login</h2>
        <form onSubmit={handleSubmit(onSubmit)}>
          {/* Email Field */}
          <div className="input-group">
            <input
              type="email"
              placeholder="Email"
              {...register("email")}
              className={errors.email ? "invalid" : ""}
              onFocus={() => clearErrors("email")}
              title="Enter a valid email address"
            />
            <p className="error">{errors.email?.message}</p>
          </div>

          {/* Password Field */}
          <div className="input-group">
            <div className="password-wrapper">
              <input
                type={showPassword ? "text" : "password"}
                placeholder="Password"
                {...register("password")}
                className={errors.password ? "invalid" : ""}
                onFocus={() => clearErrors("password")}
                title="Use at least 8 characters, including numbers and symbols"
              />
              <button
                type="button"
                onClick={togglePassword}
                className="toggle-password"
                tabIndex={-1}
              >
                {showPassword ? "Hide" : "Show"}
              </button>
            </div>
            <p className="hint">Use at least 8 characters, including numbers and symbols.</p>
            <p className="error">{errors.password?.message}</p>
          </div>

          {/* Forgot Password (Non-functional for now) */}
          <div className="forgot-link">
            <a href="#">Forgot Password?</a>
          </div>

          <button
            type="submit"
            className="btn"
            disabled={loading || !isValid}
          >
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>

        <p className="link">
          Don't have an account? <a href="/register">Register</a>
        </p>
      </div>
    </div>
  );
};

export default Login;
