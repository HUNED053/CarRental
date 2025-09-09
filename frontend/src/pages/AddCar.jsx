import React, { useState } from "react";
import axios from 'axios';
import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import { createCar } from "../services/carService";
import { toast } from "react-toastify";
import "../App.css"; // Import global styles

const carTypes = ["SEDAN", "SUV", "HATCHBACK", "UNIVERSAL"]; // Car type options


const schema = yup.object().shape({
  brand: yup.string().required("Brand is required"),
  model: yup.string().required("Model is required"),
  type: yup.string().oneOf(carTypes, "Invalid car type").required("Car type is required"),
  dailyFee: yup.number().min(1, "Fee must be at least $1").required("Daily Fee is required"),
  inventory: yup.number().min(1, "Inventory must be at least 1").required("Inventory is required"),
});

const AddCar = () => {
  const [image, setImage] = useState(null);
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: yupResolver(schema),
  });
  const onSubmit = async (data) => {
    const carData = {
      brand: data.brand,
      model: data.model,
      type: data.type,
      dailyFee: data.dailyFee,
      inventory: data.inventory,
    };
    
    const formData = new FormData();
    formData.append("car", new Blob([JSON.stringify(carData)], { type: "application/json" }));
    formData.append("image", data.carImage[0]);
    
    // Log the contents to double-check
    for (let pair of formData.entries()) {
      console.log(`${pair[0]}:`, pair[1]);
    }
    
    try {
      setLoading(true);
      const response = await axios.post("http://localhost:8080/cars", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      console.log("Response from backend:", response); // Log response from backend
      toast.success("Car added successfully!");
      navigate("/cars");
    } catch (error) {
      console.error("Error during car addition:", error.response || error.message); // Log error details
      toast.error("Failed to add car.");
    } finally {
      setLoading(false);
    }
  };
  
  
  return (
    <div className="form-container">
      <div className="form-box">
        <h2>Add New Car</h2>
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="input-group">
            <input type="text" placeholder="Brand" {...register("brand")} />
            <p className="error">{errors.brand?.message}</p>
          </div>

          <div className="input-group">
            <input type="text" placeholder="Model" {...register("model")} />
            <p className="error">{errors.model?.message}</p>
          </div>

          <div className="input-group">
            <select {...register("type")}>
              <option value="">Select Car Type</option>
              {carTypes.map((type) => (
                <option key={type} value={type}>
                  {type}
                </option>
              ))}
            </select>
            <p className="error">{errors.type?.message}</p>
          </div>

          <div className="input-group">
            <input type="number" placeholder="Daily Fee" {...register("dailyFee")} />
            <p className="error">{errors.dailyFee?.message}</p>
          </div>

          <div className="input-group">
            <input type="number" placeholder="Inventory" {...register("inventory")} />
            <p className="error">{errors.inventory?.message}</p>
          </div>
          <div className="input-group">
            <input 
              type="file" 
              accept="image/*" 
              {...register("carImage", { required: "Car image is required" })} 
            />
            <p className="error">{errors.carImage?.message}</p>
          </div>

          <button type="submit" className="btn" disabled={loading}>
            {loading ? "Adding..." : "Add Car"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default AddCar;
