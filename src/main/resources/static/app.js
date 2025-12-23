async function sendMeasurement() {
  const sensorId = document.getElementById("sensorId").value;
  const value = parseFloat(document.getElementById("sensorValue").value);

  await fetch("/api/data/measure", {
    // note: relative URL
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ sensorId, value }),
  });

  loadMeasurements();
}

async function loadMeasurements() {
  const sensorId = document.getElementById("viewSensorId").value;

  const res = await fetch(`/api/data/recent/${sensorId}`);
  const data = await res.json();

  const table = document.getElementById("dataTable");
  table.innerHTML = `
        <tr>
          <th>Timestamp</th>
          <th>Value</th>
        </tr>
    `;
  data.forEach((m) => {
    table.innerHTML += `
          <tr>
            <td>${m.timestamp}</td>
            <td>${m.value}</td>
          </tr>
        `;
  });
}

async function loadLatestState() {
  const sensorId = document.getElementById("controlSensorId").value;

  try {
    const res = await fetch(`/api/control/latest-state/${sensorId}`);

    if (!res.ok) {
      throw new Error(`HTTP error! status: ${res.status}`);
    }

    const state = await res.json();

    // Auto-populate the input fields
    document.getElementById("yk").value = state.yk.toFixed(2);
    document.getElementById("yk1").value = state.yk1.toFixed(2);
    document.getElementById("uk").value = state.uk.toFixed(2);

    console.log("Latest state loaded:", state);
  } catch (error) {
    console.error("Error loading latest state:", error);
    alert("Failed to load latest state: " + error.message);
  }
}

async function computeControl() {
  const reference = parseFloat(document.getElementById("ref").value);
  const yk = parseFloat(document.getElementById("yk").value);
  const yk1 = parseFloat(document.getElementById("yk1").value);
  const uk = parseFloat(document.getElementById("uk").value);
  const sensorId = document.getElementById("controlSensorId").value;

  try {
    const res = await fetch("/api/control/compute", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ reference, yk, yk1, uk, sensorId }),
    });

    if (!res.ok) {
      throw new Error(`HTTP error! status: ${res.status}`);
    }

    const result = await res.json();
    document.getElementById("controlOutput").innerText =
      result.control.toFixed(2);
    console.log("Control computed successfully:", result);
  } catch (error) {
    console.error("Error computing control:", error);
    document.getElementById("controlOutput").innerText =
      "Error: " + error.message;
  }
}
