import axios from "axios";

export function apiErrorMessage(error: unknown, fallback: string) {
  return axios.isAxiosError(error) ? error.response?.data?.message || fallback : fallback;
}

export function LookupSelect({ id, label, value, onChange, options, loading = false, failed = false,
  disabled = false, placeholder = "Select an option", emptyMessage, retry }: {
  id: string; label: string; value: string; onChange: (value: string) => void;
  options: { value: string; label: string }[]; loading?: boolean; failed?: boolean;
  disabled?: boolean; placeholder?: string; emptyMessage?: string; retry?: () => void;
}) {
  return <div className="space-y-1.5">
    <label htmlFor={id} className="text-sm font-medium">{label}</label>
    <select id={id} aria-label={label} required value={value} onChange={event => onChange(event.target.value)}
      disabled={disabled || loading || failed || options.length === 0}
      className="w-full rounded-lg border bg-background px-3 py-2 text-sm disabled:opacity-60">
      <option value="">{loading ? "Loading options…" : failed ? "Could not load options" : options.length === 0 ? "No eligible options" : placeholder}</option>
      {options.map(option => <option key={option.value} value={option.value}>{option.label}</option>)}
    </select>
    {failed && <p role="alert" className="text-sm text-destructive">Could not load {label.toLowerCase()}. <button type="button" className="underline" onClick={retry}>Retry {label.toLowerCase()}</button></p>}
    {!loading && !failed && !disabled && options.length === 0 && emptyMessage && <p className="text-sm text-muted-foreground">{emptyMessage}</p>}
  </div>;
}
