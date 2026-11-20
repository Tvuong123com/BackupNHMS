import { useState } from "react";
import { Button } from "@/components/ui/button";
import { LookupSelect, apiErrorMessage } from "@/components/common/lookup-select";
import { useCreateAdmission } from "../hooks/use-create-admission";
import { useCompletedScreenings } from "../../pre-admission/hooks/use-completed-screenings";
import { useFacilityForSelect } from "../hooks/use-facility-select";
import { useDialogStore } from "@/store/use-dialog-store";

export const CreateAdmissionForm = () => {
  const screenings = useCompletedScreenings();
  const facilities = useFacilityForSelect();
  const [screeningId, setScreeningId] = useState("");
  const [facilityId, setFacilityId] = useState("");
  const [roomId, setRoomId] = useState("");
  const [bedId, setBedId] = useState("");
  const [date, setDate] = useState("");
  const create = useCreateAdmission();
  const close = useDialogStore(state => state.close);
  const screeningOptions = screenings.data?.data ?? [];
  const facilityOptions = facilities.data?.data ?? [];
  const facility = facilityOptions.find(item => String(item.id) === facilityId);
  const rooms = (facility?.rooms ?? []).filter(room => room.beds.length > 0);
  const room = rooms.find(item => String(item.id) === roomId);
  const beds = room?.beds ?? [];
  const eligible = screeningOptions.some(item => String(item.id) === screeningId) && !!facility && beds.some(item => String(item.id) === bedId);
  return <form className="space-y-4" onSubmit={event => {
    event.preventDefault();
    if (!eligible || !date) return;
    create.mutate({ preAdmissionScreeningId: Number(screeningId), facilityId: Number(facilityId), bedId: Number(bedId), admissionDate: date }, { onSuccess: close });
  }}>
    <LookupSelect id="admission-screening" label="Pre-Admission Screening" value={screeningId} onChange={setScreeningId}
      loading={screenings.isPending} failed={screenings.isError} retry={() => void screenings.refetch()} disabled={create.isPending}
      options={screeningOptions.map(item => ({ value: String(item.id), label: `#${item.id} · ${item.residentName}` }))}
      emptyMessage="No completed, current screening is available for a resident awaiting admission. Complete the screening first." />
    {screeningOptions.length === 0 && !screenings.isPending && <a href="/admin/pre-admission" onClick={close} className="text-sm text-blue-600 underline">Open Pre-Admission Screenings</a>}
    <LookupSelect id="admission-facility" label="Facility" value={facilityId} onChange={value => { setFacilityId(value); setRoomId(""); setBedId(""); }}
      loading={facilities.isPending} failed={facilities.isError} retry={() => void facilities.refetch()} disabled={create.isPending}
      options={facilityOptions.map(item => ({ value: String(item.id), label: item.name }))} emptyMessage="No facilities are configured." />
    <LookupSelect id="admission-room" label="Room" value={roomId} onChange={value => { setRoomId(value); setBedId(""); }}
      disabled={!facility || create.isPending} options={rooms.map(item => ({ value: String(item.id), label: `${item.roomNumber} · ${item.beds.length} available bed(s)` }))}
      placeholder="Select a room with available beds" emptyMessage="This facility has no rooms with available beds." />
    <LookupSelect id="admission-bed" label="Bed" value={bedId} onChange={setBedId} disabled={!room || create.isPending}
      options={beds.map(item => ({ value: String(item.id), label: `${room?.roomNumber} · Bed ${item.bedNumber}` }))} emptyMessage="This room has no available beds." />
    <label className="block text-sm font-medium" htmlFor="admission-date">Admission Date</label>
    <input id="admission-date" type="date" required disabled={create.isPending} value={date} onChange={event => setDate(event.target.value)} className="w-full rounded-lg border px-3 py-2 text-sm" />
    {create.isError && <p role="alert" className="text-sm text-destructive">{apiErrorMessage(create.error, "Could not create admission. Refresh the choices; the bed or screening may no longer be available.")}</p>}
    <Button type="submit" disabled={create.isPending || !eligible || !date}>{create.isPending ? "Creating…" : "Create"}</Button>
  </form>;
};
