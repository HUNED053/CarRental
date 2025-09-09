import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import axios from "../services/api";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import { useEffect, useRef, useState } from "react";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
const schema = yup.object().shape({
  email: yup
    .string()
    .email("Invalid email format")
    .required("Email is required"),
  firstName: yup
    .string()
    .required("First name is required")
    .matches(/^[A-Za-z]+$/, "First name must only contain alphabets"),
  lastName: yup
    .string()
    .required("Last name is required")
    .matches(/^[A-Za-z]+$/, "Last name must only contain alphabets"),
  password: yup
    .string()
    .required("Password is required")
    .min(8, "Use at least 8 characters")
    .matches(/[a-z]/, "Include at least one lowercase letter")
    .matches(/[A-Z]/, "Include at least one uppercase letter")
    .matches(/[0-9]/, "Include at least one number")
    .matches(/[@$!%*?&#]/, "Include at least one special character"),
  repeatPassword: yup
    .string()
    .required("Confirm your password")
    .oneOf([yup.ref("password")], "Passwords do not match"),
});

const Register = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showRepeatPassword, setShowRepeatPassword] = useState(false);

  const firstNameRef = useRef(null);

  useEffect(() => {
    firstNameRef.current?.focus();
  }, []);

  const {
    register,
    handleSubmit,
    formState: { errors, isValid },
    reset,
    watch,
  } = useForm({
    mode: "onChange",
    resolver: yupResolver(schema),
  });

  const onSubmit = async (data) => {
    setLoading(true);
    try {
      const formattedData = {
        ...data,
        firstName: capitalize(data.firstName),
        lastName: capitalize(data.lastName),
      };
  
      await axios.post("/register", formattedData);
      toast.success("Account created successfully!");
      reset();
      navigate("/login");
    } catch (error) {
      console.error("Server error response:", error.response?.data); // keep for debug
  
      if (error.response?.status === 409) {
        toast.error("Email already exists");
      } else {
        toast.error("Registration failed. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };
  

  const capitalize = (text) =>
    text.charAt(0).toUpperCase() + text.slice(1).toLowerCase();

  return (
    <div className="form-container">
      <div className="form-box">
        <h2>Create an Account</h2>
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          {/* Email */}
          <div className={`input-group ${errors.email ? "error-border" : ""}`}>
            <input
              type="email"
              placeholder="Email"
              {...register("email")}
              title="Please enter a valid email (e.g., user@example.com)"
            />
            <p className="error">{errors.email?.message}</p>
          </div>

          {/* First Name */}
          <div className={`input-group ${errors.firstName ? "error-border" : ""}`}>
          <input
              type="text"
              placeholder="First Name"
              title="Only alphabets allowed"
              {...register("firstName")}
              onBlur={(e) => {
                e.target.value = capitalize(e.target.value);
              }}
              ref={(e) => {
                register("firstName").ref(e);
                firstNameRef.current = e;
              }}
            />

            <p className="error">{errors.firstName?.message}</p>
          </div>

          {/* Last Name */}
          <div className={`input-group ${errors.lastName ? "error-border" : ""}`}>
            <input
              type="text"
              placeholder="Last Name"
              {...register("lastName")}
              title="Only alphabets allowed"
              onBlur={(e) => {
                e.target.value = capitalize(e.target.value);
              }}
            />
            <p className="error">{errors.lastName?.message}</p>
          </div>

          {/* Password */}
          <div className={`input-group ${errors.password ? "error-border" : ""}`}>
            <div className="password-wrapper">
              <input
                type={showPassword ? "text" : "password"}
                placeholder="Password"
                {...register("password")}
                title="Use 8+ characters with a mix of upper/lowercase, numbers & symbols"
              />
              <span
                className="toggle-visibility"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? <FaEyeSlash /> : <FaEye />}
              </span>
            </div>
            <p className="error">{errors.password?.message}</p>
          </div>

          {/* Confirm Password */}
          <div
            className={`input-group ${errors.repeatPassword ? "error-border" : ""}`}
          >
            <div className="password-wrapper">
              <input
                type={showRepeatPassword ? "text" : "password"}
                placeholder="Repeat Password"
                {...register("repeatPassword")}
              />
              <span
                className="toggle-visibility"
                onClick={() => setShowRepeatPassword(!showRepeatPassword)}
              >
                {showRepeatPassword ? <FaEyeSlash /> : <FaEye />}
              </span>
            </div>
            <p className="error">{errors.repeatPassword?.message}</p>
          </div>

          <button type="submit" className="btn" disabled={!isValid || loading}>
            {loading ? "Registering..." : "Register"}
          </button>
        </form>

        <p className="link">
          Already have an account? <a href="/login">Login</a>
        </p>
      </div>
    </div>
  );
};

export default Register;
