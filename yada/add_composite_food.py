import json

def add_composite_food_from_json(json_data, file_path="composite_foods.txt"):
    """
    Add a composite food from JSON data to the composite_foods.txt file
    
    Format in the file:
    Name|search_terms|Component1:servings;Component2:servings
    
    Accepts either a single food object or an array of food objects
    """
    # Check if we have a list/array of foods or a single food
    if isinstance(json_data, list):
        # Process each food item in the list
        success_count = 0
        for food_item in json_data:
            if add_single_composite_food(food_item, file_path):
                success_count += 1
        
        print(f"Added {success_count} out of {len(json_data)} composite foods")
        return success_count == len(json_data)
    else:
        # Process a single food item
        return add_single_composite_food(json_data, file_path)
def add_single_composite_food(food_data, file_path="yada/composite_foods.txt"):
    """Helper function to add a single composite food item"""
    # Parse the JSON data if it's a string
    if isinstance(food_data, str):
        try:
            food_data = json.loads(food_data)
        except json.JSONDecodeError:
            print("Error: Invalid JSON data")
            return False
    
    # Extract information from JSON
    food_id = food_data.get("id", "")
    food_name = food_data.get("name", "")
    components = food_data.get("components", [])
    
    if not food_name or not components:
        print(f"Error: Missing required fields (name or components) for {food_id}")
        return False
    
    # Generate search terms (using name and lowercased name as default)
    search_terms = [food_name.lower(), food_name]
    search_terms_str = ",".join(search_terms)
    
    # Format components string
    component_parts = []
    for component in components:
        name = component.get("name", "")
        servings = component.get("servings", 0)
        if name and servings:
            component_parts.append(f"{name}:{servings}")
    
    components_str = ";".join(component_parts)
    
    # Create the entry in the required format
    entry = f"{food_name}|{search_terms_str}|{components_str}\n"
    
    # Append to the file
    try:
        with open(file_path, "a") as file:
            file.write(entry)
        print(f"Successfully added {food_name} to {file_path}")
        return True
    except Exception as e:
        print(f"Error writing to file: {e}")
        return False

# Example usage with your data
sample_data = [
  {
    "id": "composite001",
    "name": "Peanut Butter Sandwich",
    "components": [
      { "name": "White Bread Slice", "servings": 2 },
      { "name": "Peanut Butter", "servings": 1 }
    ]
  },
  {
    "id": "composite002",
    "name": "PB&J Sandwich",
    "components": [
      { "name": "White Bread Slice", "servings": 2 },
      { "name": "Peanut Butter", "servings": 1 },
      { "name": "Jam", "servings": 1 }
    ]
  },
  {
    "id": "composite003",
    "name": "Paneer Wrap",
    "components": [
      { "name": "Roti", "servings": 1 },
      { "name": "Paneer", "servings": 1 },
      { "name": "Onion", "servings": 0.5 },
      { "name": "Tomato", "servings": 0.5 }
    ]
  },
  {
    "id": "composite004",
    "name": "Dal Chawal",
    "components": [
      { "name": "Dal", "servings": 1 },
      { "name": "Cooked Rice", "servings": 1 }
    ]
  },
  {
    "id": "composite005",
    "name": "Naan Paneer",
    "components": [
      { "name": "Naan", "servings": 1 },
      { "name": "Paneer", "servings": 1 },
      { "name": "Onion", "servings": 0.5 }
    ]
  },
  {
    "id": "composite006",
    "name": "Veg Sandwich",
    "components": [
      { "name": "Whole Wheat Bread Slice", "servings": 2 },
      { "name": "Butter", "servings": 0.5 },
      { "name": "Tomato", "servings": 0.5 },
      { "name": "Cucumber", "servings": 0.5 },
      { "name": "Onion", "servings": 0.5 }
    ]
  },
  {
    "id": "composite007",
    "name": "Egg Salad",
    "components": [
      { "name": "Boiled Egg", "servings": 1 },
      { "name": "Cucumber", "servings": 0.5 },
      { "name": "Onion", "servings": 0.5 },
      { "name": "Mayonnaise", "servings": 1 }
    ]
  },
  {
    "id": "composite008",
    "name": "Chana Roti",
    "components": [
      { "name": "Roti", "servings": 1 },
      { "name": "Cooked Chickpeas", "servings": 1 }
    ]
  },
  {
    "id": "composite009",
    "name": "Ghee Roti",
    "components": [
      { "name": "Roti", "servings": 1 },
      { "name": "Ghee", "servings": 1 }
    ]
  },
  {
    "id": "composite010",
    "name": "Paneer Rice Bowl",
    "components": [
      { "name": "Cooked Rice", "servings": 1 },
      { "name": "Paneer", "servings": 1 },
      { "name": "Tomato", "servings": 0.5 },
      { "name": "Onion", "servings": 0.5 }
    ]
  }
]


# Call the function with your sample data
add_composite_food_from_json(sample_data)