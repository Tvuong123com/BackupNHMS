import { UserTab } from "@/features/admin/settings/tabs/user-tab";

const UserPage = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-gray-900 sm:text-3xl">
          User Management
        </h1>
        <p className="mt-1 text-sm text-gray-500">
          Manage system users, staff accounts, role assignments, and access statuses.
        </p>
      </div>

      <div className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
        <UserTab />
      </div>
    </div>
  );
};

export default UserPage;
