import Navigation from "@/components/Navigation";
import { Firstpart } from "./Firstpart";
import { Secondpart } from "./Secondpart";

export default function Page() {
  return (
    <div>
      <div>
        <Navigation />
      </div>
      <div>
        {/* bg-yellow-700 and bg-yellow-50 */}
        <div id="explore">
          <Firstpart />
        </div>
        {/* bg-red-700 and bg-red-50 */}
        <div id="howtouse">
          <Secondpart />
        </div>
      </div>
    </div>
  );
}
