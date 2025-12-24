async function sendMeasurement() {
  const sensorId = document.getElementById("sensorId").value;
  const value = parseFloat(document.getElementById("sensorValue").value);

  await fetch("/api/data/measure", {
    // note: relative URL
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ sensorId, value }),
  });

  // Automatically switch viewSensorId to match the sensor we just sent data to
  document.getElementById("viewSensorId").value = sensorId;

  // Now load measurements for this sensor
  loadMeasurements();
}

async function loadMeasurements() {
  const sensorId = document.getElementById("viewSensorId").value;

  console.log(`=== Loading measurements for sensor: ${sensorId} ===`);

  try {
    const url = `/api/data/recent/${sensorId}`;
    console.log(`Fetching from URL: ${url}`);

    const res = await fetch(url);

    if (!res.ok) {
      throw new Error(`HTTP error! status: ${res.status}`);
    }

    const data = await res.json();
    console.log(`Received ${data.length} records:`, data);

    const table = document.getElementById("dataTable");
    table.innerHTML = `
        <tr>
          <th>Sensor: ${sensorId}</th>
          <th>Timestamp</th>
          <th style="padding-left: 20px;">Value</th>
        </tr>
    `;

    if (data.length === 0) {
      table.innerHTML += `
        <tr>
          <td colspan="3" style="text-align: center; color: gray;">No data available for ${sensorId}</td>
        </tr>
      `;
    } else {
      data.forEach((m) => {
        // Format timestamp: "2025-12-23T20:02:15.824" -> "2025-12-23  20:02:15"
        const formattedTime = m.timestamp
          ? m.timestamp.replace("T", "  ").split(".")[0]
          : "N/A";

        table.innerHTML += `
          <tr>
            <td>${m.sensorId}</td>
            <td>${formattedTime}</td>
            <td style="padding-left: 20px;">${m.value}</td>
          </tr>
        `;
      });
    }

    console.log(
      `✓ Successfully loaded ${data.length} measurements for ${sensorId}`
    );
  } catch (error) {
    console.error("Error loading measurements:", error);
    alert("Failed to load measurements: " + error.message);
  }
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
    const controlValue = result.control;

    document.getElementById("controlOutput").innerText =
      controlValue.toFixed(2);

    // Generate instruction based on control value
    let instruction = "";
    let instructionColor = "";

    if (Math.abs(controlValue) < 0.5) {
      instruction = "✓ System near setpoint. Maintain current settings.";
      instructionColor = "green";
    } else if (controlValue > 0) {
      instruction = `⬆ Increase input by ${Math.abs(controlValue).toFixed(
        2
      )} units to reach setpoint.`;
      instructionColor = "#00ff0dff";
    } else {
      instruction = `⬇ Decrease input by ${Math.abs(controlValue).toFixed(
        2
      )} units to reach setpoint.`;
      instructionColor = "#ff6200ff";
    }

    document.getElementById("controlInstruction").innerText = instruction;
    document.getElementById("controlInstruction").style.color =
      instructionColor;

    console.log("Control computed successfully:", result);
  } catch (error) {
    console.error("Error computing control:", error);
    document.getElementById("controlOutput").innerText =
      "Error: " + error.message;
  }
}
