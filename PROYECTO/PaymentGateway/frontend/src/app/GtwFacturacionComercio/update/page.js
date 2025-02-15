"use client";

import { useState } from "react";
import { useRouter } from "next/navigation"; // Importamos useRouter para navegación

const UpdatePage = () => {
  const [form, setForm] = useState({
    codFacturacionComercio: "FAC123",
    codComercio: "COM001",
    fechaInicio: "2023-01-01",
    fechaFin: "2023-12-31",
    transaccionesProcesadas: "100",
    transaccionesAutorizadas: "90",
    transaccionesRechazadas: "10",
    transaccionesReversadas: "5",
    codComision: "COM123",
    valor: "500",
    estado: "Activo",
    codigoFacturacion: "COD001",
    fechaFacturacion: "2023-11-01",
    fechaPago: "", // Campo editable
  });

  const router = useRouter(); // Inicializamos el router para redirigir

  const handleChange = (e) => {
    const { name, value } = e.target;

    // Validar fechaPago para que no sea anterior a la fecha actual
    if (name === "fechaPago") {
      const today = new Date().toISOString().split("T")[0]; // Fecha actual en formato YYYY-MM-DD
      if (value < today) {
        alert("La fecha de pago no puede ser anterior a la fecha actual.");
        return;
      }
    }

    setForm({ ...form, [name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    // Validar que el campo fechaPago no esté vacío
    if (!form.fechaPago) {
      alert("El campo 'Fecha de Pago' no puede estar vacío.");
      return;
    }

    console.log("Formulario actualizado:", form);

    router.push("/GtwFacturacionComercio/components"); // Redirige a la página principal después de guardar
  };

  const handleCancel = () => {
    router.push("/GtwFacturacionComercio/components"); // Redirige a la página principal sin guardar
  };

  return (
    <main
      style={{
        maxWidth: "600px",
        margin: "2rem auto",
        padding: "2rem",
        backgroundColor: "#f9fafb",
        borderRadius: "8px",
        boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
      }}
    >
      <h1
        style={{
          textAlign: "center",
          color: "#1e40af",
          marginBottom: "1.5rem",
        }}
      >
        Actualizar Factura
      </h1>
      <form
        onSubmit={handleSubmit}
        style={{
          display: "flex",
          flexDirection: "column",
          gap: "1rem",
        }}
      >
        {Object.keys(form).map((field) => (
          <div
            key={field}
            style={{ display: "flex", flexDirection: "column", gap: "0.5rem" }}
          >
            <label
              htmlFor={field}
              style={{
                fontWeight: "bold",
                color: "#1f2937",
              }}
            >
              {field
                .replace(/([A-Z])/g, " $1")
                .replace(/^./, (str) => str.toUpperCase())}
            </label>
            <input
              id={field}
              type={field.includes("fecha") ? "date" : "text"}
              name={field}
              value={form[field]}
              onChange={handleChange}
              disabled={field !== "fechaPago"} // Deshabilita todos los campos excepto fechaPago
              style={{
                padding: "10px",
                fontSize: "1rem",
                borderRadius: "4px",
                border: "1px solid #d1d5db",
                backgroundColor: field !== "fechaPago" ? "#e5e7eb" : "#ffffff",
                color: field !== "fechaPago" ? "#6b7280" : "#000000",
              }}
              placeholder={`Ingrese ${field
                .replace(/([A-Z])/g, " $1")
                .toLowerCase()}`}
            />
          </div>
        ))}
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            marginTop: "1rem",
          }}
        >
          <button
            type="submit"
            style={{
              backgroundColor: "#10b981",
              color: "#ffffff",
              padding: "10px 20px",
              fontSize: "1rem",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer",
            }}
          >
            Guardar
          </button>
          <button
            type="button"
            onClick={handleCancel}
            style={{
              backgroundColor: "#ef4444",
              color: "#ffffff",
              padding: "10px 20px",
              fontSize: "1rem",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer",
            }}
          >
            Cancelar
          </button>
        </div>
      </form>
    </main>
  );
};

export default UpdatePage;
