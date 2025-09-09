import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import { getCarById, updateCar } from "../services/carService";
import { toast } from "react-toastify";
import "../App.css"; // Import global styles

const schema = yup.object().shape({
  brand: yup.string().required("Brand is required"),
  model: yup.string().required("Model is required"),
  type: yup.string().required("Car type is required"),
  dailyFee: yup.number().min(1, "Fee must be at least $1").required("Daily Fee is required"),
  inventory: yup.number().min(1, "Inventory must be at least 1").required("Inventory is required"),
});

const EditCar = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const { register, handleSubmit, setValue, formState: { errors } } = useForm({
    resolver: yupResolver(schema),
  });

  useEffect(() => {
    const fetchCarDetails = async () => {
      try {
        const car = await getCarById(id);
        setValue("brand", car.brand);
        setValue("model", car.model);
        setValue("type", car.type);
        setValue("dailyFee", car.dailyFee);
        setValue("inventory", car.inventory);
      } catch (error) {
        toast.error("Error fetching car details.");
      }
    };

    fetchCarDetails();
  }, [id, setValue]);

  const onSubmit = async (data) => {
    setLoading(true);
    try {
      await updateCar(id, data);
      toast.success("Car updated successfully!");
      navigate("/cars");
    } catch (error) {
      toast.error("Failed to update car.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-container">
      <div className="form-box">
        <h2>Edit Car</h2>
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
            <input type="text" placeholder="Type (SUV, Sedan, etc.)" {...register("type")} />
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

          <button type="submit" className="btn" disabled={loading}>
            {loading ? "Updating..." : "Update Car"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default EditCar;
