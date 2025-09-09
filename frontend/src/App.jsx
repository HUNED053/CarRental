import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import PaymentSuccessPage from "./pages/PaymentSuccessPage";
import PaymentCancelPage from "./pages/PaymentCancelPage";
import Home from "./pages/Home";
import Cars from "./pages/Cars";
import CarDetails from "./pages/CarDetails";
import AddCar from "./pages/AddCar";
import EditCar from "./pages/EditCar";
import RentCar from "./pages/RentCar";
import RentalHistory from "./pages/RentalHistory";
import Payments from "./pages/Payments";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import { ToastContainer } from "react-toastify";
import Accounts from "./pages/Accounts";
import "react-toastify/dist/ReactToastify.css";
import "./App.css";
import AdminPayments from "./pages/AdminPayments";

function App() {
  return (
    <AuthProvider>
      <Router>
        {/* ToastContainer outside of Routes */}
        <ToastContainer position="top-right" autoClose={3000} />
        
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/success" element={<PaymentSuccessPage />} />
          <Route path="/cancel" element={<PaymentCancelPage />} />

          {/* Protected Routes */}
          <Route path="/" element={<ProtectedRoute><Home /></ProtectedRoute>} />
          <Route path="/cars" element={<ProtectedRoute><Cars /></ProtectedRoute>} />
          <Route path="/cars/:id" element={<ProtectedRoute><CarDetails /></ProtectedRoute>} />
          <Route path="/add-car" element={<ProtectedRoute><AddCar /></ProtectedRoute>} />
          <Route path="/edit-car/:id" element={<ProtectedRoute><EditCar /></ProtectedRoute>} />
          <Route path="/rent-car/:id" element={<ProtectedRoute><RentCar /></ProtectedRoute>} />
          <Route path="/rental-history" element={<ProtectedRoute><RentalHistory /></ProtectedRoute>} />
          <Route path="/payments" element={<ProtectedRoute><Payments /></ProtectedRoute>} />
          <Route path="/accounts" element={<ProtectedRoute><Accounts /></ProtectedRoute>} />
          <Route path="/adminpayments" element={<ProtectedRoute><AdminPayments/></ProtectedRoute>} />


        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
