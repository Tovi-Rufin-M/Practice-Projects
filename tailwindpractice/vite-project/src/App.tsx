export default function App() {
  return (
    <>
    <div className="min-h-screen bg-gray-100 flex items-center justify-center">
      <div className="bg-white p-8 rounded-xl shadow-xl w-80">
        <h1 className="text-2xl font-bold text-blue-600">
          Welcome
        </h1>

        <p className="text-gray-600 mt-3">
          My first Tailwind component.
        </p>

        <button className="mt-5 bg-blue-500 hover:bg-blue-600 text-white px-5 py-2 rounded-lg">
          Get Started
        </button>
      </div>
    </div>

    <div className="min-h-screen bg-gray-600 flex items-center flex-col">
      <h1 className="h-full text-2xl font-bold text-white w-full text-center">
        Another Section
      </h1>
      <div id="card 1" className="bg-green-100 p-8 rounded-xl shadow-xl w-80 h-auto justify-center-safe">
        <div id="image" className="bg-gray-300 border-2 border-dashed rounded-xl w-full h-80" />
        <h1 id="name"className="text-xl font-bold text-gray-800 m-auto text-center">Card 1</h1>
        <p id="description" className="text-gray-600 mt-3">
          This is a description for Card 1. It can be a brief summary of the content or purpose of the card.
        </p>
        <button id="action" className="mt-5 bg-blue-500 hover:bg-blue-600 text-white px-5 py-2 rounded-lg">
          Learn More
        </button>
      </div>
    </div>
    </>
  );
}