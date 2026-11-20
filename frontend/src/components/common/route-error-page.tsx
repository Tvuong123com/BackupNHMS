import { isRouteErrorResponse, useRouteError } from "react-router";

export const RouteErrorPage = () => {
  const error = useRouteError();
  const notFound = isRouteErrorResponse(error) && error.status === 404;
  const message = error instanceof Error ? error.message : "";
  const domChanged = message.includes("insertBefore") || message.includes("removeChild");

  return (
    <main className="flex min-h-screen items-center justify-center bg-slate-50 px-6" translate="no">
      <section className="max-w-lg space-y-5 text-center" aria-labelledby="route-error-title">
        <h1 id="route-error-title" className="text-3xl font-bold text-slate-900">
          {notFound ? "Page not found" : "This page could not finish updating"}
        </h1>
        <p className="text-slate-600">
          {notFound
            ? "The address may have changed. Return to the home page to continue."
            : "Reload this page to restore the interface and try again."}
        </p>
        {domChanged && (
          <p className="text-sm text-slate-600">
            If browser translation is active, return to the original language before reloading.
          </p>
        )}
        <div className="flex flex-wrap justify-center gap-3">
          {!notFound && (
            <button type="button" onClick={() => window.location.reload()}
              className="rounded-lg bg-blue-600 px-5 py-3 font-semibold text-white hover:bg-blue-700">
              <span>Reload page</span>
            </button>
          )}
          <a href="/" className="rounded-lg border border-slate-300 px-5 py-3 font-semibold text-slate-700 hover:bg-white">
            <span>Go to home page</span>
          </a>
        </div>
      </section>
    </main>
  );
};
