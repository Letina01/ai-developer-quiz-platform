export default function DomainCard({ title, description }) {
  return (
    <div className="glass-card domain-card">
      <h3>{title}</h3>
      <p>{description}</p>
    </div>
  );
}
