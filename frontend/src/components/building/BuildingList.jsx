import BuildingCard from "./BuildingCard";

export default function BuildingList({ buildings, onEdit, onDelete }) {
  if (!buildings || buildings.length === 0) {
    return (
      <div
        style={{
          padding: "40px 0",
          textAlign: "center",
          color: "#6b7280",
          fontSize: "14px",
        }}
      >
        아직 등록된 건물이 없습니다.
      </div>
    );
  }

  return (
    <div>
      {buildings.map((building) => (
        <BuildingCard
          key={building.id}
          building={building}
          onEdit={onEdit}
          onDelete={onDelete}
        />
      ))}
    </div>
  );
}
