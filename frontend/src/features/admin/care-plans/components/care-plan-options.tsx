import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Input } from "@/components/ui/input";
import { ChevronDown, Search, Plus, LayoutGrid } from "lucide-react";
import { useState } from "react";

const statusOptions = [
  "All",
  "Draft",
  "Pending review",
  "Active",
  "Review due",
  "Needs update",
  "Archived",
];

const reviewOptions = ["All", "Overdue", "Within 7 days", "Within 30 days"];

type CarePlanOptionsProps = {
  onSearch: (residentName?: string, status?: string) => void;
};

export default function CarePlanOptions(props: CarePlanOptionsProps) {
  const [status, setStatus] = useState("All");
  const [review, setReview] = useState("All");
  const [residentName, setResidentName] = useState("");

  return (
    <div className="flex flex-col xl:flex-row xl:items-center justify-between gap-3 bg-white p-3 rounded-xl border border-slate-200/80 shadow-xs">
      {/* Search and Dropdowns */}
      <div className="flex flex-col sm:flex-row sm:flex-wrap items-stretch sm:items-center gap-2 flex-1">
        {/* Search Bar */}
        <div className="relative w-full sm:max-w-xs md:max-w-sm">
          <Search className="absolute left-3 top-2.5 size-4 text-slate-400" />
          <Input
            className="w-full bg-slate-50 border border-slate-200 rounded-lg pl-9 pr-4 py-2 h-9 text-sm text-slate-800 placeholder:text-slate-400 focus:outline-none focus:border-blue-500 focus:bg-white transition-all shadow-none"
            placeholder="Search by resident name..."
            value={residentName}
            onChange={(e) => setResidentName(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                props.onSearch(
                  residentName,
                  status === "All"
                    ? undefined
                    : status.toUpperCase().replace(/\s+/g, "_")
                );
              }
            }}
          />
        </div>

        {/* Filters Container */}
        <div className="flex flex-wrap items-center gap-2">
          {/* Status Dropdown */}
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <button className="w-full sm:w-auto flex items-center justify-between gap-2 bg-white border border-slate-200 rounded-lg px-3 py-2 h-9 text-sm font-medium text-slate-700 hover:bg-slate-50 min-w-[130px] text-left cursor-pointer outline-none">
                <span>Status: {status}</span>
                <ChevronDown className="size-3.5 text-slate-400" />
              </button>
            </DropdownMenuTrigger>

            <DropdownMenuContent className="w-[160px] bg-white border border-slate-200 rounded-lg shadow-md z-30 py-1">
              {statusOptions.map((option) => (
                <DropdownMenuItem
                  key={option}
                  className={`w-full text-left px-3 py-2 text-sm transition-all hover:bg-slate-50 cursor-pointer ${
                    status === option ? 'text-blue-600 font-semibold bg-blue-50/50' : 'text-slate-600'
                  }`}
                  onClick={() => {
                    setStatus(option);
                    props.onSearch(
                      residentName,
                      option === "All"
                        ? undefined
                        : option.toUpperCase().replace(/\s+/g, "_")
                    );
                  }}
                >
                  {option}
                </DropdownMenuItem>
              ))}
            </DropdownMenuContent>
          </DropdownMenu>

          {/* Review Dropdown */}
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <button className="w-full sm:w-auto flex items-center justify-between gap-2 bg-white border border-slate-200 rounded-lg px-3 py-2 h-9 text-sm font-medium text-slate-700 hover:bg-slate-50 min-w-[140px] text-left cursor-pointer outline-none">
                <span>Review: {review}</span>
                <ChevronDown className="size-3.5 text-slate-400" />
              </button>
            </DropdownMenuTrigger>

            <DropdownMenuContent className="w-[160px] bg-white border border-slate-200 rounded-lg shadow-md z-30 py-1">
              {reviewOptions.map((option) => (
                <DropdownMenuItem
                  key={option}
                  className={`w-full text-left px-3 py-2 text-sm transition-all hover:bg-slate-50 cursor-pointer ${
                    review === option ? 'text-blue-600 font-semibold bg-blue-50/50' : 'text-slate-600'
                  }`}
                  onClick={() => setReview(option)}
                >
                  {option}
                </DropdownMenuItem>
              ))}
            </DropdownMenuContent>
          </DropdownMenu>

          {/* Board View Button */}
          <button className="flex items-center justify-center gap-1.5 bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 h-9 text-sm font-medium text-slate-600 hover:bg-slate-100 transition-all cursor-pointer">
            <LayoutGrid className="size-4" />
            <span>Board</span>
          </button>
        </div>
      </div>

      {/* Add New Care Plan Button */}
      <button className="flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg px-4 py-2.5 h-9 text-sm font-semibold transition-all shadow-xs cursor-pointer active:scale-98 whitespace-nowrap">
        <Plus className="size-4" />
        New Care Plan
      </button>
    </div>
  );
}
