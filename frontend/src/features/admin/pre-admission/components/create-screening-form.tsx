import { useState } from "react";
import { Button } from "@/components/ui/button";
import { LookupSelect, apiErrorMessage } from "@/components/common/lookup-select";
import { useCreateScreening } from "../hooks/use-create-screening";
import { usePendingResidents } from "../hooks/use-pending-residents";
import { useDialogStore } from "@/store/use-dialog-store";

export const CreateScreeningForm = () => {
  const lookup = usePendingResidents();
  const residents = lookup.data?.data ?? [];
  const [residentId, setResidentId] = useState("");
  const create = useCreateScreening();
  const close = useDialogStore(state => state.close);
  return <form className="space-y-4" onSubmit={event => {
    event.preventDefault();
    if (!residents.some(resident => String(resident.id) === residentId)) return;
    create.mutate({ residentId: Number(residentId) }, { onSuccess: close });
  }}>
    <LookupSelect id="screening-resident" label="Resident" value={residentId} onChange={setResidentId}
      loading={lookup.isPending} failed={lookup.isError} retry={() => void lookup.refetch()} disabled={create.isPending}
      options={residents.map(resident => ({ value: String(resident.id), label: `${resident.fullName} · #${resident.id}` }))}
      placeholder="Select a pending resident" emptyMessage="No pending resident is eligible for a new screening. Review existing drafts or add a resident first." />
    <a className="text-sm text-blue-600 underline" href="/admin/residents" onClick={close}>Open Residents</a>
    {create.isError && <p role="alert" className="text-sm text-destructive">{apiErrorMessage(create.error, "Could not create screening. Check the selected resident and try again.")}</p>}
    <Button type="submit" disabled={create.isPending || !residents.some(resident => String(resident.id) === residentId)}>{create.isPending ? "Creating…" : "Create"}</Button>
  </form>;
};
