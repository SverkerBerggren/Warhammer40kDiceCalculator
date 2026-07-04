"use client";
import { ComposedChart, Bar, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts";

interface DistributionChartProps {
  samples: number[]; // raw per-simulation results, e.g. one entry per simulation run
  title: string;
  color?: string;
  lineColor?: string;
}

interface ChartDatum {
  outcome: number;
  probability: number;   // P(exactly outcome), %
  cumulative: number;    // P(outcome or more), %
}

function CustomTooltip({ active, payload, label }: any) {
  if (!active || !payload || payload.length === 0) return null;
  const datum: ChartDatum = payload[0].payload;
  return (
    <div className="bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-600 rounded px-3 py-2 shadow-md text-sm">
      <p className="font-semibold mb-1">Outcome: {label}</p>
      <p style={{ color: payload.find((p: any) => p.dataKey === "probability")?.color }}>
        {datum.probability.toFixed(1)}% chance of exactly {label}
      </p>
      <p style={{ color: payload.find((p: any) => p.dataKey === "cumulative")?.color }}>
        {datum.cumulative.toFixed(1)}% chance of {label} or more
      </p>
    </div>
  );
}

export default function DistributionChart({
  samples,
  title,
  color = "#3b82f6",
  lineColor = "#f97316",
}: DistributionChartProps) {
  const totalSims = samples.length;

  // Bin the raw samples into a histogram: outcome value -> count
  const counts = new Map<number, number>();
  for (const value of samples) {
    counts.set(value, (counts.get(value) ?? 0) + 1);
  }

  const maxOutcome = samples.length > 0 ? Math.max(...samples) : 0;

  // Build per-outcome probability first (dense 0..maxOutcome so gaps show as 0% bars)
  const perOutcomeProbability = Array.from({ length: maxOutcome + 1 }, (_, outcome) =>
    totalSims > 0 ? ((counts.get(outcome) ?? 0) / totalSims) * 100 : 0
  );

  // Cumulative "X or more" = reverse running sum of probabilities, clamped to [0, 100]
  // to eliminate floating-point drift (e.g. 100.00000000000001 from repeated addition)
  const chartData: ChartDatum[] = [];
  let runningTotal = 0;
  for (let outcome = maxOutcome; outcome >= 0; outcome--) {
    runningTotal += perOutcomeProbability[outcome];
    chartData.unshift({
      outcome,
      probability: perOutcomeProbability[outcome],
      cumulative: Math.min(100, runningTotal),
    });
  }

  // Bar axis scales to the tallest bar (with headroom), not fixed to 100
  const maxBarValue = Math.max(...perOutcomeProbability, 0);
  const barAxisMax = maxBarValue > 0 ? Math.ceil(maxBarValue * 1.2 * 10) / 10 : 1;

  return (
    <div className="w-full">
      <h2 className="text-center font-semibold mb-2">{title}</h2>
      <ResponsiveContainer width="100%" height={250}>
        <ComposedChart data={chartData} margin={{ top: 10, right: 30, left: 0, bottom: 20 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="outcome" label={{ value: "Outcome", position: "insideBottom", offset: -10 }} />

          {/* Left axis: histogram bars, scaled to their own max */}
          <YAxis
            yAxisId="bars"
            domain={[0, barAxisMax]}
            tickFormatter={(v) => `${v}%`}
            label={{ value: "P(exactly X)", angle: -90, position: "insideLeft" }}
          />

          {/* Right axis: cumulative line, fixed 0-100 since it's a true cumulative probability */}
          <YAxis
            yAxisId="cumulative"
            orientation="right"
            domain={[0, 100]}
            tickFormatter={(v) => `${v}%`}
            label={{ value: "P(X or more)", angle: 90, position: "insideRight" }}
          />

          <Tooltip content={<CustomTooltip />} />

          <Bar yAxisId="bars" dataKey="probability" fill={color} radius={[4, 4, 0, 0]} />
          <Line
            yAxisId="cumulative"
            type="monotone"
            dataKey="cumulative"
            stroke={lineColor}
            strokeWidth={2}
            dot={false}
            activeDot={{ r: 5 }}
          />
        </ComposedChart>
      </ResponsiveContainer>
    </div>
  );
}