export default function Button({
  children,
  onClick,
  variant = "default",
  style = {},
  disabled = false,
  type = "button",
}) {
  const baseStyle = {
    padding: "12px 32px",
    fontSize: "14px",
    fontWeight: "500",
    borderRadius: "12px",
    border: "1px solid #d1d5db",
    backgroundColor: "#ffffff",
    color: "#111827",
    cursor: disabled ? "not-allowed" : "pointer", 
    boxShadow: "0 2px 6px rgba(0,0,0,0.08)",
    opacity: disabled ? 0.5 : 1,           
  };

  const variants = {
    primary: {
      backgroundColor: "#2563eb",
      color: "#ffffff",
      border: "none",
    },
    danger: {             
      backgroundColor: "#ef4444",
      color: "#ffffff",
      border: "none",
    },
  };

  return (
    <button
      type={type}
      disabled={disabled}                         
      onClick={disabled ? undefined : onClick}  
      style={{
        ...baseStyle,
        ...(variants[variant] || {}),
        ...style,
      }}
    >
      {children}
    </button>
  );
}
